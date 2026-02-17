package com.myauth.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {
    @Bean
    public CommonsRequestLoggingFilter logFilter(Environment env) {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("REQUEST DATA : ");

        if (isDev) {
            filter.setIncludeQueryString(true);
            filter.setIncludePayload(true);
            filter.setIncludeClientInfo(true);
            filter.setIncludeHeaders(true);
        } else {
            filter.setIncludeQueryString(false);
            filter.setIncludePayload(false);
            filter.setIncludeClientInfo(false);
            filter.setIncludeHeaders(false);
        }

        return filter;
    }
}