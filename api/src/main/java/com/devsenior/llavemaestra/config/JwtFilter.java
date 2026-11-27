package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Se ejecuta en CADA peticion, antes de llegar al controlador:
// 1. Lee el encabezado "Authorization: Bearer <token>"
// 2. Valida el token y saca el email
// 3. Busca la cuenta en la base y revisa que SIGA ACTIVA (asi un bloqueo aplica de inmediato)
// 4. Le dice a Spring Security quien es y que permisos tiene
// Se crea en SecurityConfig (no es @Component) para que Spring no lo registre dos veces.
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String encabezado = request.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            String token = encabezado.substring(7);
            String email = jwtService.extraerEmail(token);

            if (email != null) {
                Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

                // Solo autenticamos si la cuenta existe y NO esta bloqueada
                if (usuario != null && usuario.isEnabled()) {
                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }
        }

        // Si no hubo autenticacion, Spring Security respondera 401 en las rutas protegidas
        chain.doFilter(request, response);
    }
}
