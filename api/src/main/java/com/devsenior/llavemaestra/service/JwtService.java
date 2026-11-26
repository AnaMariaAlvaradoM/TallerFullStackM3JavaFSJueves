package com.devsenior.llavemaestra.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret:default_secret_key_llave_maestra_2026_super_segura_dev_senior}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        List<String> permisos = usuario.getRol().getPermisos().stream()
                .map(Enum::name)
                .toList();

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .claim("permisos", permisos)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(expiration)))
                .signWith(getKey())
                .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esValido(String token, String email) {
        String sub = extraerUsername(token);
        Instant vencimiento = extraerClaims(token).getExpiration().toInstant();
        return email.equals(sub) && vencimiento.isAfter(Instant.now());
    }
}
