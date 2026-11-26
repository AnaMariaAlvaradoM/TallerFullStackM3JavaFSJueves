package com.devsenior.llavemaestra.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long horasExpiracion;

    public JwtService(
            @Value("${jwt.secret:llave-maestra-clave-secreta-de-desarrollo-2026}") String secret,
            @Value("${jwt.expiration-hours:24}") long horasExpiracion
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.horasExpiracion = horasExpiracion;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        List<String> permisos = usuario.getRol().getPermisos().stream()
                .map(Permiso::name)
                .toList();

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .claim("permisos", permisos)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(horasExpiracion, ChronoUnit.HOURS)))
                .signWith(signingKey)
                .compact();
    }

    public Claims extraerYValidarClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerEmail(String token) {
        return extraerYValidarClaims(token).getSubject();
    }
}