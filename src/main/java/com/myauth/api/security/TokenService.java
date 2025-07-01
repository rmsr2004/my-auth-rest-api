package com.myauth.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String deviceId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .claim("deviceId", deviceId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenValidation validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new TokenValidation(true, "", claims);
        } catch (SecurityException e) {
            return new TokenValidation(false, "Invalid JWT signature", null);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return new TokenValidation(false, "Invalid JWT token", null);
        } catch (ExpiredJwtException e) {
            return new TokenValidation(false, "Expired JWT token", null);
        } catch (UnsupportedJwtException e) {
            return new TokenValidation(false, "Unsupported JWT token", null);
        }
    }
}
