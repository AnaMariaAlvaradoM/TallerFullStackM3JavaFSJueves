package com.devsenior.llavemaestra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration //Indica a Spring que esta clase define beans
@EnableWebSecurity //Activa la integracion de Spring Security con la capa web
@EnableMethodSecurity // Habilita @PreAuthorize y similares en los controladores o servicios. SIN ESTO, ESAS ANOTACIONES SE IGNORAN EN SILIENCIO
public class SecurityConfig {

    private  final JwtFilter jwtFilter; // Spring entrega el JwFilter que lee el token del heder y autentica la peticion
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean // BCrypt cifra con salt aleatorio, dos cifrados de la misma clase dan hashes distintos
     public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder();}

    /*CORS bloquea peticiones entre Origenes distintos, aquí se autoriza a Angular a llamar a la API con los metodos y cualquier header.
    * "/**" aplica la regla a todas la rutas
    * OPTIONS porque el navegador manda una petición "preflight" antes de las peticiones complejas */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;

    }

    /*Componente que verifica usuario y contraseña, Spring se encarga de construirlo con los bean que encuentra (UserDetailService y PasswordEncoder)*/
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return  config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) //Activa CORS
                .csrf(csrf -> csrf.disable()) // Protege las sesiones basadas en cookies
                .authorizeHttpRequests(auth -> auth //Son las reglas y se evaluan en orden, gana la primera que coincide
                        .requestMatchers("/api/auth/**").permitAll() //publico, ya que es para el login y registro
                        .requestMatchers("/error").permitAll() // publico, Spring dirige internamente a esta ruta cuando algo falla
                        .requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll() //publico
                        .anyRequest().authenticated() //Para las demas rutas exige estar autenticado, debe de ir al final
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session ->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    }

