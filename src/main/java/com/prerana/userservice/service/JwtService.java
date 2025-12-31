package com.prerana.userservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "SecretKey1Prerana122123456789012345678901234567890";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public boolean validateToken(String token) {
        try {
            // This single line does several checks automatically:
            // 1. Checks if the signature is valid (MalformedJwtException, SignatureException)
            // 2. Checks if the token is expired (ExpiredJwtException)
            // 3. Checks if the token is generally valid/supported (IllegalArgumentException, UnsupportedJwtException)
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
    // Generate JWT
    public String generateToken(String mobile,String userType,String role,Long userId) {
        return Jwts.builder()
                .subject(mobile)
                .claim("userType",userType)
                .claim("role",role)
                .claim("userId",userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7200000)) // 2 hour
                .signWith(KEY)
                .compact();
    }

    // Extract mobile from JWT
    public String extractMobile(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    // Check expiry
    public boolean isExpired(String token) {
        Date expiry = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiry.before(new Date());
    }
}
