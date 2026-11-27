package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

// Las reglas generales de seguridad de la API
@Configuration
@EnableMethodSecurity // activa @PreAuthorize("hasAuthority('...')") en los controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService,
                                       UsuarioRepository usuarioRepository) throws Exception {
        http
                // API REST con token: no hay formularios ni cookies, asi que CSRF no aplica
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsAngular()))
                // Sin sesion en el servidor: cada peticion trae su token
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Todo lo demas pide token. El permiso fino va en cada controlador.
                        .anyRequest().authenticated())
                // Sin token (o token invalido / cuenta bloqueada) -> 401 con { codigo, mensaje, fecha }
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) ->
                                escribirError(response, 401, "Debes iniciar sesión para continuar"))
                        .accessDeniedHandler((request, response, ex) ->
                                escribirError(response, 403, "No tienes permiso para realizar esta acción")))
                .addFilterBefore(new JwtFilter(jwtService, usuarioRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Las contrasenas se guardan cifradas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Le dice a Spring Security como buscar una cuenta por su email
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe la cuenta " + email));
    }

    // Permite que Angular (http://localhost:4200) le hable a la API
    private CorsConfigurationSource corsAngular() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", config);
        return fuente;
    }

    // Escribe el JSON de error a mano (estos errores pasan ANTES de llegar a los controladores)
    private void escribirError(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        response.setStatus(codigo);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"codigo\":" + codigo
                + ",\"mensaje\":\"" + mensaje
                + "\",\"fecha\":\"" + LocalDateTime.now() + "\"}");
    }
}
