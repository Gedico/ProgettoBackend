package ProgettoINSW.backend.security;

import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.Utente;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.repository.UtenteRepository;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccountRepository accountRepository;
    private final UtenteRepository utenteRepository;

    public SecurityConfig(AccountRepository accountRepository,
                          UtenteRepository utenteRepository) {
        this.accountRepository = accountRepository;
        this.utenteRepository = utenteRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/registerUtente").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/api/auth/oauth2/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(new DefaultOAuth2UserService()))
                        .successHandler(this::handleOAuthSuccess)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // ======== DETERMINA PROVIDER =========
    private String getProviderFromRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("google")) return "google";
        if (uri.contains("github")) return "github";
        if (uri.contains("facebook")) return "facebook";
        return "unknown";
    }

    private void handleOAuthSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {

        String provider = getProviderFromRequest(request);

        switch (provider) {
            case "google" -> handleGoogleSuccess(response, authentication);
            case "github" -> handleGitHubSuccess(response, authentication);
            case "facebook" -> handleFacebookSuccess(response, authentication);
            default -> response.sendError(400, "Provider OAuth non supportato");
        }
    }

    // ========= GOOGLE ==========
    private void handleGoogleSuccess(HttpServletResponse response,
                                     Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> a = user.getAttributes();

        String email = (String) a.get("email");
        String nome = (String) a.getOrDefault("given_name", "Utente");
        String cognome = (String) a.getOrDefault("family_name", "Google");

        processOAuthUser(email, nome, cognome, response);
    }

    // ========= GITHUB ==========
    private void handleGitHubSuccess(HttpServletResponse response,
                                     Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> a = user.getAttributes();

        // Token OAuth2
        String tokenOAuth = (String) authentication.getCredentials();

        String email = (String) a.get("email");

        // Se GitHub NON fornisce email → la recuperiamo tramite API
        if (email == null) {
            try {
                RestTemplate rest = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "token " + tokenOAuth);
                HttpEntity<String> entity = new HttpEntity<>("", headers);

                ResponseEntity<List> emailResponse = rest.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        List.class
                );

                List<Map<String, Object>> emails = emailResponse.getBody();

                if (emails != null) {
                    for (Map<String, Object> e : emails) {
                        if (Boolean.TRUE.equals(e.get("primary")) &&
                                Boolean.TRUE.equals(e.get("verified"))) {
                            email = (String) e.get("email");
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback finale
        if (email == null) {
            email = a.get("login") + "@github.fake";
        }

        String nome = (String) a.get("name");

        if (nome == null || nome.isBlank()) {
            nome = (String) a.get("login"); // username GitHub
        }

        if (nome == null || nome.isBlank()) {
            nome = "GitHubUser";
        }

        String cognome = "GitHub";

        processOAuthUser(email, nome, cognome, response);
    }

    // ========= FACEBOOK ==========
    private void handleFacebookSuccess(HttpServletResponse response,
                                       Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> a = user.getAttributes();

        String email = (String) a.get("email");
        if (email == null) email = UUID.randomUUID() + "@facebook.fake";

        String nome = (String) a.getOrDefault("first_name", "FacebookUser");
        String cognome = (String) a.getOrDefault("last_name", "");

        processOAuthUser(email, nome, cognome, response);
    }

    // ========= CREA ACCOUNT + UTENTE ==========
    private void processOAuthUser(String email,
                                  String nome,
                                  String cognome,
                                  HttpServletResponse response) throws IOException {

        Account account = accountRepository.findByMailIgnoreCase(email)
                .orElseGet(() -> {
                    Account nuovo = new Account();
                    nuovo.setMail(email);
                    nuovo.setNome(nome);
                    nuovo.setCognome(cognome);
                    nuovo.setPassword(passwordEncoder().encode(UUID.randomUUID().toString()));
                    nuovo.setRuolo(Role.UTENTE);
                    return accountRepository.save(nuovo);   // <-- viene fatto solo per NUOVO utente
                });

        // ❗ Nessun save() se già esiste -> EVITI entity detached!

        String token = JwtUtil.generateToken(account.getMail(), account.getRuolo().name());

        response.sendRedirect("http://localhost:4200/oauth-callback?token=" + token);
    }
}