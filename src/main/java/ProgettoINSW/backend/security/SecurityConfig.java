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
                        .requestMatchers(HttpMethod.POST, "/api/auth/registerUtente").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/auth/delete/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/logout").authenticated()

                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoint per il profilo

                        .requestMatchers(HttpMethod.GET, "/profilo/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/profilo/update").authenticated()

                        .requestMatchers( "/profilo/**").authenticated()

                        // Endpoint per la gestione degli immobili (solo agenti)

                        .requestMatchers(HttpMethod.GET, "/api/inserzioni").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inserzioni/ricerca").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inserzioni/{id}").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/inserzioni/crea").hasAnyRole("AGENTE","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inserzioni/modifica/**").hasAnyRole("AGENTE","ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inserzioni/elimina/**").hasAnyRole("AGENTE","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/inserzioni/caricaFoto").hasAnyRole("AGENTE","ADMIN")


                        // Endpoint per le proposte

                        .requestMatchers(HttpMethod.GET, "/api/proposte/stato").hasAnyRole("AGENTE","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/proposte/{id}/stato").hasAnyRole("AGENTE","ADMIN")


                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

