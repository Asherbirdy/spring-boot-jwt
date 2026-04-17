package com.app.security.service;

import com.app.security.dto.AuthLoginResponse;
import com.app.security.dto.AuthRegisterResponse;
import com.app.security.dto.LoginRequest;
import com.app.security.dto.RegisterRequest;

public interface AuthService {

    AuthRegisterResponse register(RegisterRequest registerRequest);

    AuthLoginResponse login(LoginRequest loginRequest);

    void logout();
}
