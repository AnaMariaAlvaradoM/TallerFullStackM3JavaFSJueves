package com.devsenior.llavemaestra.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.service.AuthService;
import com.devsenior.llavemaestra.service.JwtService;
import com.devsenior.llavemaestra.dto.AuthResponse;
import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.dto.RegistroRequest;
import com.devsenior.llavemaestra.exception.ConflictoException;
import com.devsenior.llavemaestra.exception.DatoInvalidoException;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentificador();
        if (identifier == null || identifier.isBlank() || request.password() == null || request.password().isBlank()) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByEmail(identifier)
                .or(() -> usuarioRepository.findByUsername(identifier))
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        if (!usuario.isActivo()) {
            throw new DisabledException("Tu cuenta está desactivada. Comunícate con la administración.");
        }

        String token = jwtService.generarToken(usuario);
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse registrar(RegistroRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new DatoInvalidoException("El nombre es obligatorio");
        }
        if (request.nombre().length() > 80) {
            throw new DatoInvalidoException("El nombre no puede tener más de 80 caracteres");
        }
        if (request.email() == null || request.email().isBlank() || !request.email().contains("@")) {
            throw new DatoInvalidoException("El email no tiene un formato válido");
        }
        if (request.password() == null || request.password().length() < 8) {
            throw new DatoInvalidoException("La contraseña debe tener al menos 8 caracteres");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ConflictoException("Ya existe una cuenta con el email " + request.email());
        }

        Usuario nuevo = Usuario.builder()
                .nombre(request.nombre().trim())
                .email(request.email().trim().toLowerCase())
                .username(request.email().trim().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.CLIENTE) // Siempre cliente
                .activo(true)
                .creadoEn(LocalDateTime.now(ZoneId.systemDefault()))
                .build();

        usuarioRepository.save(nuevo);
        String token = jwtService.generarToken(nuevo);
        return new AuthResponse(token);
    }
}


