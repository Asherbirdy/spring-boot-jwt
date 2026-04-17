package com.app.security.service;

import com.app.security.dto.LoginRequest;
import com.app.security.dto.RegisterRequest;

import java.util.Map;

public interface AuthService {

    Map<String, Object> register(RegisterRequest registerRequest);

    Map<String, Object> login(LoginRequest loginRequest);

    void logout(String memberId);
}
