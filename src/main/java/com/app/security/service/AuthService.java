package com.app.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface AuthService {

    Map<String, Object> register(String name, String email, String password,
                                  HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> login(String email, String password,
                               HttpServletRequest request, HttpServletResponse response);

    void logout(String memberId, HttpServletResponse response);
}
