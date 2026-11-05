package com.devsenior.llavemaestra.dto;

// Lo que responde el login: { "token": "xxx.yyy.zzz" }
public record TokenResponse(String token) {
}
