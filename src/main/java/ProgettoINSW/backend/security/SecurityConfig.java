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

                        // ====== ENDPOINT AUTH LOCALI ======
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerUtente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerAgente").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerAdmin").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/auth/delete/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

                        // Lasciamo /api/auth/** aperto per ora (come avevi già)
                        .requestMatchers("/api/auth/**").permitAll()

                        // ====== PROFILO ======
                        .requestMatchers(HttpMethod.GET, "/profilo/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/profilo/update").authenticated()
                        .requestMatchers("/profilo/**").authenticated()

                        // ====== INSERZIONI ======
                        .requestMatchers(HttpMethod.GET, "/api/inserzioni").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inserzioni/ricerca").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inserzioni/{id}").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/inserzioni/crea").hasAnyRole("AGENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inserzioni/modifica/**").hasAnyRole("AGENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inserzioni/elimina/**").hasAnyRole("AGENTE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/inserzioni/caricaFoto/{id}").hasAnyRole("AGENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inserzioni/eliminaFoto/{id}").hasAnyRole("AGENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inserzioni/modificaStato/{id}").hasAnyRole("AGENTE", "ADMIN")

                        // ====== PROPOSTE ======
                        .requestMatchers(HttpMethod.GET, "/api/proposte/stato").hasAnyRole("AGENTE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/proposte/{id}/stato").hasAnyRole("AGENTE", "ADMIN")

                        // ====== OAUTH2 (GOOGLE, ecc.) ======
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // Il resto per ora lo lasciamo aperto
                        .anyRequest().permitAll()
                )

                // ====== CONFIGURAZIONE OAUTH2 ======
                .oauth2Login(oauth -> oauth
                        // non usiamo una vera loginPage perché Angular gestisce il redirect,
                        // ma Spring vuole comunque una stringa
                        .loginPage("/api/auth/oauth2/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new DefaultOAuth2UserService())
                        )
                        .successHandler(this::oAuth2SuccessHandler)
                )

                // ====== FILTRO JWT (come prima) ======
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /*
     * Handler chiamato DOPO un login OAuth2 riuscito (es. Google).
     * Qui:
     *  - leggiamo i dati dell'utente dal provider
     *  - cerchiamo/creiamo Account
     *  - generiamo il JWT con JwtUtil
     *  - reindirizziamo Angular con ?token=...
     */
    private void oAuth2SuccessHandler(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Per Google: email, given_name, family_name, sub (id univoco)
        String email = (String) attributes.get("email");
        String givenName = (String) attributes.getOrDefault("given_name", "");
        String familyName = (String) attributes.getOrDefault("family_name", "");

        if (email == null || email.isBlank()) {
            // caso limite: senza email non possiamo creare un account
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email non fornita dal provider OAuth2");
            return;
        }

        Optional<Account> existingOpt = accountRepository.findByMailIgnoreCase(email);

        Account account;
        if (existingOpt.isPresent()) {
            account = existingOpt.get();
        } else {
            // 👉 REGISTRAZIONE AUTOMATICA UTENTE SOCIAL
            account = new Account();
            account.setNome(givenName.isBlank() ? "Utente" : givenName);
            account.setCognome(familyName.isBlank() ? "Social" : familyName);
            account.setMail(email);

            // 👇 password obbligatoria nel DB → generiamo una fittizia (non usata)
            String randomPassword = UUID.randomUUID().toString();
            account.setPassword(passwordEncoder().encode(randomPassword));

            account.setNumero(null); // Google non fornisce numero
            account.setRuolo(Role.UTENTE); // ruolo di default per registrazione via social

            accountRepository.save(account);
        }

        // 👉 Genera JWT usando la tua util (mail + ruolo)
        String token = JwtUtil.generateToken(
                account.getMail(),
                account.getRuolo().name()
        );

        // 👉 Redirect verso Angular con il token nella querystring
        String redirectUrl = "http://localhost:4200/oauth-callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}

