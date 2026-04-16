package com.app.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/member")
@RestController
public class MemberController {

@GetMapping("/showMe")
public ResponseEntity<Map<String, Object>> showMe() {
    Map<String, Object> map = Map.of("msg", "Hello World!");
    return ResponseEntity.status(HttpStatus.OK).body(map);
}
}
