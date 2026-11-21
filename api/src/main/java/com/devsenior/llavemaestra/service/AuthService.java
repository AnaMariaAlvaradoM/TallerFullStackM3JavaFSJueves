package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.AuthResponse;
import com.devsenior.llavemaestra.dto.LoginRequest;


public interface AuthService {

    AuthResponse login(LoginRequest request);



}
