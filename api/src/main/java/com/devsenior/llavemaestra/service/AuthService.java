package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.config.JwtService;
import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.dto.TokenResponse;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// El login: revisa email, contrasena y que la cuenta este activa; si todo va bien, entrega el token
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse login(LoginRequest datos) {
        // Mismo mensaje si el email no existe o si la clave esta mal:
        // asi nadie puede averiguar que correos estan registrados.
        Usuario usuario = usuarioRepository.findByEmail(datos.email())
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(datos.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        // Cuenta bloqueada por Hernan -> 401 (lo traduce ManejadorGlobalExcepciones)
        if (!usuario.isEnabled()) {
            throw new DisabledException("Cuenta bloqueada");
        }

        return new TokenResponse(jwtService.generarToken(usuario));
    }
}
