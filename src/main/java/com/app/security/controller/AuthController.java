package com.app.security.controller;

import com.app.security.dto.LoginRequest;
import com.app.security.dto.RegisterRequest;
import com.app.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 這邊放無需驗證的 controller
@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        Map<String, Object> user = authService.register(registerRequest.getName(), 
                                                        registerRequest.getEmail(),
                                                        registerRequest.getPassword(), 
                                                        request, 
                                                        response);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        Map<String, Object> user = authService.login(loginRequest.getEmail(), 
                                                     loginRequest.getPassword(),
                                                     request, response);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
