package com.app.security.controller;
import com.app.security.service.AuthService;
import com.app.security.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RequestMapping("/member")
@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @GetMapping("/showMe")
    public ResponseEntity<Map<String, Object>> showMe(Authentication authentication) {
        String memberId = (String) authentication.getCredentials();
        Map<String, Object> memberInfo = memberService.showMemberInfo(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication,
                                                      HttpServletResponse response) {
        String memberId = (String) authentication.getCredentials();
        authService.logout(memberId, response);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("msg", "LOGOUT_SUCCESS"));
    }
}
