package com.devsenior.llavemaestra.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.llavemaestra.config.JwtService;
import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.dto.RegistroRequest;
import com.devsenior.llavemaestra.dto.TokenResponse;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new DisabledException("Cuenta inactiva");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        return new TokenResponse(jwtService.generarToken(usuario));
    }

    @Override
    @Transactional
    public TokenResponse registro(RegistroRequest request) {
        String emailLimpio = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmailIgnoreCase(emailLimpio)) {
            throw new ReglaNegocioException("Ya existe una cuenta registrada con ese email");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.nombre().trim());
        nuevo.setEmail(emailLimpio);
        nuevo.setPassword(passwordEncoder.encode(request.password()));
        nuevo.setRol(Rol.CLIENTE);
        nuevo.setActivo(true);

        Usuario guardado = usuarioRepository.save(nuevo);
        return new TokenResponse(jwtService.generarToken(guardado));
    }
}