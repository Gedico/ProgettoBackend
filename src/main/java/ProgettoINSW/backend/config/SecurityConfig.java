package ProgettoINSW.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disattiva il CSRF (permette DELETE e POST da Postman)
                .authorizeHttpRequests(auth -> auth

                        //.requestMatchers("/api/auth/registerAgente" , "/api/auth/registerAdmin").hasRole("ADMIN") // ✅ Solo ADMIN può registrare Agente e Admin

                        .requestMatchers("/api/auth/**").permitAll() // ✅ Tutte le rotte /api/auth sono libere
                        .anyRequest().permitAll() // tutto il resto libero
                )
                .formLogin(form -> form.disable())  // Disattiva login form automatico
                .httpBasic(httpBasic -> httpBasic.disable()); // Disattiva autenticazione base

        return http.build();
    }
}

