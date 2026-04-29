package com.klu.ProjectYAT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.klu.ProjectYAT.dto.OTPRequest;
import com.klu.ProjectYAT.dto.RegisterRequest;
import com.klu.ProjectYAT.model.User;
import com.klu.ProjectYAT.repository.UserRepository;
import com.klu.ProjectYAT.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    // REGISTER
    public Map<String, Object> register(RegisterRequest request) {

        String otp = String.valueOf((int)(Math.random() * 9000) + 1000);
        Map<String, Object> response = new HashMap<>();

        // Check if user already exists
        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            response.put("success", false);
            response.put("message", "Email already exists");
            return response;
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtp(otp);
        user.setVerified(false);
        user.setRole(request.getRole() == null ? "student" : request.getRole());

        userRepo.save(user);

        try {
            emailService.sendOtp(user.getEmail(), otp);
            response.put("success", true);
            response.put("message", "OTP sent to email. Check email for OTP.");
            response.put("otpDeliveryStatus", "sent");
        } catch (Exception e) {
            // Account created but email delivery failed. Do NOT expose the OTP in the API response.
            response.put("success", true);
            response.put("message", "Account created, but OTP email could not be delivered. Contact support or configure email access.");
            response.put("otpDeliveryStatus", "failed");
            // Log the exception server-side so operators can debug SMTP issues
            e.printStackTrace();
        }

        return response;
    }

    // VERIFY OTP
    public String verifyOtp(OTPRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() != null && user.getOtp().equals(request.getOtp())) {
            user.setVerified(true);
            user.setOtp(null);
            userRepo.save(user);
            return "Verified successfully";
        }

        return "Invalid OTP";
    }

    // LOGIN — returns a Map so the frontend gets id, name, email, role in one shot
    public java.util.Map<String, Object> login(com.klu.ProjectYAT.dto.LoginRequest request) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        User user = userRepo.findByEmail(request.getEmail())
                .orElse(null);

        if(user == null) {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return response;
        }

        if (!user.isVerified()) {
            response.put("success", false);
            response.put("message", "Verify your account first!");
            return response;
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Success — include everything the frontend needs to resolve identity
            String accessToken = jwtUtil.generateAccessToken(
                    String.valueOf(user.getId()),
                    user.getEmail(),
                    user.getRole()
            );
            String refreshToken = jwtUtil.generateRefreshToken(
                    String.valueOf(user.getId()),
                    user.getEmail()
            );
            
            long accessTokenExpiresAt = jwtUtil.getTokenExpiryTimestamp(accessToken);
            long refreshTokenExpiresAt = jwtUtil.getTokenExpiryTimestamp(refreshToken);
            
            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("id", user.getId());
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            // NEW: Include tokens with expiry info for client-side session management
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("accessTokenExpiresAt", accessTokenExpiresAt);
            response.put("refreshTokenExpiresAt", refreshTokenExpiresAt);
            return response;
        }

        response.put("success", false);
        response.put("message", "Invalid credentials");
        return response;
    }
}
