package com.myauth.conf.spring.logging;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info(
            "START REQUEST | Path: {} | Method: {} | From: {}", 
            request.getRequestURI(), 
            request.getMethod(),
            request.getRemoteAddr()
        );

        String requestId = UUID.randomUUID().toString();

        MDC.put("requestId", requestId);

        Long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        
        Long duration = System.currentTimeMillis() - startTime;

        log.info(
            "END REQUEST | Path: {} | Method: {} | Status: {} | Time: {}ms", 
            request.getRequestURI(), 
            request.getMethod(), 
            response.getStatus(), 
            duration
        );
        
        MDC.clear();
    }
}
