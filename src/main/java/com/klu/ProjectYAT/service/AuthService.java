package com.klu.ProjectYAT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.klu.ProjectYAT.dto.LoginRequest;
import com.klu.ProjectYAT.dto.OTPRequest;
import com.klu.ProjectYAT.dto.RegisterRequest;
import com.klu.ProjectYAT.model.User;
import com.klu.ProjectYAT.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // REGISTER
    public String register(RegisterRequest request) throws Exception {

        String otp = String.valueOf((int)(Math.random() * 9000) + 1000);

        // Check if user already exists
        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            return "Email already exists";
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
        } catch(Exception e) {
            e.printStackTrace();
            return "Error sending OTP";
        }

        return "OTP sent to email. Check email for OTP.";
    }

    // VERIFY OTP
    public String verifyOtp(OTPRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() != null && user.getOtp().equals(request.getOtp())) {
            user.setVerified(true);
            user.setOtp(null);
            userRepo.save(user);
            return "Verified successfully! User ID: " + user.getId();
        }

        return "Invalid OTP";
    }

    // LOGIN
    public String login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElse(null);

        if(user == null) {
            return "Invalid credentials"; // "User not found" might be helpful, but we limit for security
        }

        if (!user.isVerified()) {
            return "Verify your account first!";
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Login successful! User ID: " + user.getId();
        }

        return "Invalid credentials";
    }
}
