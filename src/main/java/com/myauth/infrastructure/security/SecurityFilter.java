package com.myauth.infrastructure.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myauth.infrastructure.db.entities.User;
import com.myauth.infrastructure.db.repositories.IUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final IUserRepository userRepository;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // String path = request.getRequestURI();
        // String method = request.getMethod();

        // if ("/api/auth/register".equals(path) && "POST".equalsIgnoreCase(method)) {
        // filterChain.doFilter(request, response);
        // return;
        // }

        // if ("/swagger-ui".equals(path) && "GET".equalsIgnoreCase(method)) {
        // filterChain.doFilter(request, response);
        // return;
        // }

        try {
            String token = this.getToken(request);

            if (token != null) {
                Long userId = tokenService.validateToken(token);

                if (userId != null) {
                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null) {
                        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}
