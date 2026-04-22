package com.app.security.service.impl;

import com.app.security.dao.MemberDao;
import com.app.security.dao.TokenDao;
import com.app.security.dto.AuthLoginResponse;
import com.app.security.dto.AuthRegisterResponse;
import com.app.security.dto.LoginRequest;
import com.app.security.dto.RegisterRequest;
import com.app.security.model.Member;
import com.app.security.model.Token;
import com.app.security.security.JwtUtil;
import com.app.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class AuthServiceImpl implements AuthService {

    private final MemberDao memberDao;

    private final TokenDao tokenDao;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(MemberDao memberDao, TokenDao tokenDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.memberDao = memberDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthRegisterResponse register(RegisterRequest registerRequest) {
        String name = registerRequest.getName();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        // 檢查 email 是否已被註冊
        Member existingMember = memberDao.getMemberByEmail(email);
        if (existingMember != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_USED");
        }

        // hash 密碼並建立會員
        String hashedPassword = passwordEncoder.encode(password);
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setPassword(hashedPassword);
        member.setRole("user");

        String memberId = memberDao.createMember(member);

        // 建立 refresh token 並存入資料庫
        String refreshTokenStr = createRefreshToken(memberId);

        // 產生 JWT 並設定 Cookie
        attachCookieToResponse(memberId, name, email, "user", refreshTokenStr);

        return new AuthRegisterResponse(name, memberId, "user");
    }

    @Override
    public AuthLoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // 查詢會員
        Member member = memberDao.getMemberByEmail(email);
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "WRONG_EMAIL_OR_PASSWORD");
        }

        // 驗證密碼
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "WRONG_EMAIL_OR_PASSWORD");
        }

        String memberId = member.getMemberId();
        String name = member.getName();
        String role = member.getRole();

        // 檢查是否有有效的 refresh token
        String refreshTokenStr;
        Token existingToken = tokenDao.getValidTokenByMemberId(memberId);

        if (existingToken != null) {
            refreshTokenStr = existingToken.getRefreshToken();
        } else {
            // 建立新的 refresh token
            refreshTokenStr = createRefreshToken(memberId);
        }

        // 產生 JWT 並設定 Cookie
        attachCookieToResponse(memberId, name, email, role, refreshTokenStr);

        return new AuthLoginResponse(name, memberId, role);
    }

    @Override
    public void logout() {
        String memberId = getCurrentMemberId();
        HttpServletResponse response = getCurrentResponse();

        // 刪除該使用者所有 token
        tokenDao.deleteTokensByMemberId(memberId);

        // 清除 Cookie
        Cookie accessCookie = new Cookie("accessToken", "");
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    private ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    private HttpServletRequest getCurrentRequest() {
        return getRequestAttributes().getRequest();
    }

    private HttpServletResponse getCurrentResponse() {
        return getRequestAttributes().getResponse();
    }

    private String getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getCredentials();
    }

    private String createRefreshToken(String memberId) {
        HttpServletRequest request = getCurrentRequest();
        String refreshTokenStr = UUID.randomUUID().toString();
        Token token = new Token();
        token.setRefreshToken(refreshTokenStr);
        token.setIp(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        token.setIsValid(true);
        token.setMemberId(memberId);
        tokenDao.createToken(token);
        return refreshTokenStr;
    }

    private void attachCookieToResponse(String memberId, String name, String email, String role, String refreshTokenStr) {
        HttpServletResponse response = getCurrentResponse();
        String accessTokenJwt = jwtUtil.createAccessToken(memberId, name, email, role);
        String refreshTokenJwt = jwtUtil.createRefreshToken(memberId, email, refreshTokenStr);

        Cookie accessCookie = new Cookie("accessToken", accessTokenJwt);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (jwtUtil.getAccessTokenExpirationMs() / 1000));

        Cookie refreshCookie = new Cookie("refreshToken", refreshTokenJwt);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpirationMs() / 1000));

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
