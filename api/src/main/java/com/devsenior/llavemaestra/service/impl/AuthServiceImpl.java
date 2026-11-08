package com.devsenior.llavemaestra.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.AuthResponse;
import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AuthService;
import com.devsenior.llavemaestra.service.JwtService;

@Service 
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Si el email o la contraseña no coinciden, o la cuenta esta desactivada,
        // esta linea lanza una AuthenticationException (401)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + email));

        return new AuthResponse(jwtService.generarToken(usuario));
    }
}
