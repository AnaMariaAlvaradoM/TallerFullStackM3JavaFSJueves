package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.service.UsuarioDetailsService;
import com.devsenior.llavemaestra.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter  {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtFilter(JwtService jwtService, UsuarioDetailsService usuarioDetailsService) {
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header==null || !header.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = jwtService.extraerEmail(token);
            if (email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails usuarioDetails =  usuarioDetailsService.loadUserByUsername(email);

            if (usuarioDetails.isEnabled() && jwtService.esValido(token,usuarioDetails)){
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                usuarioDetails,null,usuarioDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
    }catch (JwtException|IllegalArgumentException| UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);

    }
}
