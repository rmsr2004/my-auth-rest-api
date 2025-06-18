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
    private int expiration;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userID) {
        return Jwts.builder()
                .setSubject(userID)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getIDFromToken(String token) {
        if (token == null) {
            return null;
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public TokenValidation validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return new TokenValidation(true, "");
        } catch (SecurityException e) {
            return new TokenValidation(false, "Invalid JWT signature");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return new TokenValidation(false, "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            return new TokenValidation(false, "Expired JWT token");
        } catch (UnsupportedJwtException e) {
            return new TokenValidation(false, "Unsupported JWT token");
        }
    }
}
