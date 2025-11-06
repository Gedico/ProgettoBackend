package ProgettoINSW.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http , JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Endpoint di registrazione e autenticazione

                        .requestMatchers(HttpMethod.POST, "/api/auth/registerAgente").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerAdmin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/delete/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/auth/registerUtente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        .requestMatchers(HttpMethod.POST,"/api/auth/logout").authenticated()

                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoint per il profilo

                        .requestMatchers("/profilo/**").authenticated()

                        // Endpoint per la gestione degli immobili (solo agenti)

                        .requestMatchers(HttpMethod.POST, "/api/immobili/crea").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/immobili/modifica/**").hasRole("AGENTE")


                        .requestMatchers(HttpMethod.GET, "/api/immobili/ricerca", "/api/immobili/{id}").permitAll()


                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

