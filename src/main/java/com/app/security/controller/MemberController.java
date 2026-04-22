package com.app.security.controller;

import com.app.security.dto.MemberInfoResponse;
import com.app.security.service.AuthService;
import com.app.security.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    private final AuthService authService;

    public MemberController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @GetMapping("/showMe")
    public ResponseEntity<MemberInfoResponse> showMe() {
        MemberInfoResponse memberInfo = memberService.showMemberInfo();
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("msg", "LOGOUT_SUCCESS"));
    }
}
