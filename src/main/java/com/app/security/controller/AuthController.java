package com.app.security.controller;

import com.app.security.dto.AuthLoginResponse;
import com.app.security.dto.AuthRegisterResponse;
import com.app.security.dto.LoginRequest;
import com.app.security.dto.RegisterRequest;
import com.app.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 這邊放無需驗證的 controller
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthRegisterResponse user = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthLoginResponse user = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
