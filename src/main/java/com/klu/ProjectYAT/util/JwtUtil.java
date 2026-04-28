package com.klu.ProjectYAT.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret-key:your-super-secret-key-change-this-in-production}")
    private String secretKey;
    
    @Value("${jwt.access-token-expiry:900000}")  // 15 minutes in ms
    private long accessTokenExpiry;
    
    @Value("${jwt.refresh-token-expiry:604800000}")  // 7 days in ms
    private long refreshTokenExpiry;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    /**
     * Generate an access token (short-lived, 15 minutes)
     */
    public String generateAccessToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("token_type", "access");
        
        return createToken(claims, email, accessTokenExpiry);
    }
    
    /**
     * Generate a refresh token (long-lived, 7 days)
     */
    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        claims.put("userId", userId);
        
        return createToken(claims, email, refreshTokenExpiry);
    }
    
    /**
     * Create a JWT token with given claims
     */
    private String createToken(Map<String, Object> claims, String subject, long expiryMs) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiresAt = new Date(now + expiryMs);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Get token expiry time in milliseconds from now
     * Returns negative if token is already expired
     */
    public long getTokenExpiryIn(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            long expiryInMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            return expiryInMs;
        } catch (JwtException e) {
            return -1; // Invalid or expired token
        }
    }
    
    /**
     * Check if token is valid (not expired, properly signed)
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    /**
     * Extract email from token
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (String) claims.get("role");
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * Get token expiry timestamp (not offset from now)
     */
    public long getTokenExpiryTimestamp(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime();
        } catch (JwtException e) {
            return -1;
        }
    }
}
