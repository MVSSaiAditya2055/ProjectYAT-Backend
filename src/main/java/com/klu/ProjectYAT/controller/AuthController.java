package com.klu.ProjectYAT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.ProjectYAT.dto.OTPRequest;
import com.klu.ProjectYAT.dto.RegisterRequest;
import com.klu.ProjectYAT.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<java.util.Map<String, Object>> register(@RequestBody RegisterRequest request) {
        java.util.Map<String, Object> response = authService.register(request);
        if (Boolean.TRUE.equals(response.get("success"))) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/verify")
    public String verify(@RequestBody OTPRequest request) {
        return authService.verifyOtp(request);
    }

    @PostMapping("/login")
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> login(@RequestBody com.klu.ProjectYAT.dto.LoginRequest request) {
        java.util.Map<String, Object> response = authService.login(request);
        if ((Boolean)response.get("success")) {
            return org.springframework.http.ResponseEntity.ok(response);
        } else {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
