package ProgettoINSW.backend.security;

import ProgettoINSW.backend.model.Account;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.repository.AccountRepository;
import ProgettoINSW.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccountRepository accountRepository;

    public SecurityConfig(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerUtente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerAgente").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerAdmin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/api/auth/oauth2/login")     // pagina unica per avviare OAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new DefaultOAuth2UserService())
                        )
                        .successHandler(this::handleOAuthSuccess)  // handler che gestisce Google / GitHub / Facebook
                )



                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // ================================
    //       HANDLER GENERALE
    // ================================
    private void handleOAuthSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {

        String provider = getProviderFromRequest(request);

        switch (provider) {
            case "google" -> handleGoogleSuccess(response, authentication);
            case "github" -> handleGitHubSuccess(response, authentication);
            case "facebook" -> handleFacebookSuccess(response, authentication);
            default -> response.sendError(400, "Provider OAuth non supportato: " + provider);
        }
    }

    private String getProviderFromRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.contains("google")) return "google";
        if (uri.contains("github")) return "github";
        if (uri.contains("facebook")) return "facebook";

        return "unknown";
    }

    // ============================================
    //                GOOGLE
    // ============================================
    private void handleGoogleSuccess(HttpServletResponse response,
                                     Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> a = user.getAttributes();

        String email = (String) a.get("email");
        String nome = (String) a.getOrDefault("given_name", "Utente");
        String cognome = (String) a.getOrDefault("family_name", "Google");

        processOAuthUser(email, nome, cognome, response);
    }

    // ============================================
    //                GITHUB
    // ============================================
    private void handleGitHubSuccess(HttpServletResponse response,
                                     Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> a = user.getAttributes();

        String email = (String) a.get("email");
        if (email == null) {
            // GitHub NON SEMPRE fornisce email → fallback con username
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

    // ============================================
    //                FACEBOOK
    // ============================================
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

    // ============================================
    //       CREAZIONE ACCOUNT + REDIRECT
    // ============================================
    private void processOAuthUser(String email,
                                  String nome,
                                  String cognome,
                                  HttpServletResponse response) throws IOException {

        Optional<Account> existing = accountRepository.findByMailIgnoreCase(email);
        Account account;

        if (existing.isPresent()) {
            account = existing.get();
        } else {
            account = new Account();
            account.setMail(email);
            account.setNome(nome);
            account.setCognome(cognome);

            String randomPassword = UUID.randomUUID().toString();
            account.setPassword(passwordEncoder().encode(randomPassword));
            account.setRuolo(Role.UTENTE);

            accountRepository.save(account);
        }

        String token = JwtUtil.generateToken(account.getMail(), account.getRuolo().name());

        response.sendRedirect("http://localhost:4200/oauth-callback?token=" + token);
    }
}

