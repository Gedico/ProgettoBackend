package ProgettoINSW.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 🔓 Disattiva CSRF (per Postman)
                .authorizeHttpRequests(authz -> authz
                        // ✅ Consenti le richieste per la registrazione/login
                        .requestMatchers("/api/auth/**").permitAll()
                        // ✅ (Opzionale) H2 Console se la usi
                        .requestMatchers("/h2-console/**").permitAll()
                        // ❌ Tutto il resto richiede autenticazione
                        .anyRequest().authenticated()
                )
                // ✅ Disattiva form login e HTTP basic
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // ✅ Nessuna gestione di sessione (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
