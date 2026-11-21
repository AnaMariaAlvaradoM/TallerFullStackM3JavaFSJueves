package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.AuthResponse;
import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.dto.RegistroRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse registrar(RegistroRequest request);
}
