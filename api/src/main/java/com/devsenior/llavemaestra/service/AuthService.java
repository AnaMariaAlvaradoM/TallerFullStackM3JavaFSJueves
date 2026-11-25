package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.dto.RegistroRequest;
import com.devsenior.llavemaestra.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse registro(RegistroRequest request);
}