package com.myauth.api.security;

import com.myauth.api.controllers.AuthController;
import com.myauth.api.dtos.device.DeviceNotFoundException;
import com.myauth.api.entities.Device;
import com.myauth.api.entities.User;
import com.myauth.api.repositories.DeviceRepository;
import com.myauth.api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final TokenService tokenService;
    private final Logger logger;

    public SecurityFilter(
            UserRepository userRepository,
            DeviceRepository deviceRepository,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.tokenService = tokenService;
        this.logger = LoggerFactory.getLogger(SecurityFilter.class);
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        TokenValidation result = this.tokenService.validateToken(token);

        if (result.isValid()) {
            String userId = result.claims().getSubject();
            String deviceId = result.claims().get("deviceId", String.class);

            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new DeviceNotFoundException("Device not found"));

            boolean deviceIsValid = device.getUser().getId().equals(userId);

            if (!deviceIsValid) {
                throw new DeviceNotFoundException("Device not found");
            }

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
