package com.myauth.config;

import org.springframework.web.bind.annotation.RestController;

import com.scalar.maven.core.ScalarProperties;
import com.scalar.maven.core.enums.ScalarLayout;
import com.scalar.maven.core.enums.ScalarTheme;
import com.scalar.maven.webmvc.ScalarWebMvcController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ScalarConfigController extends ScalarWebMvcController {

    @Override
    protected ScalarProperties configureProperties(ScalarProperties properties, HttpServletRequest request) {
        properties.setEnabled(true);
        properties.setUrl("http://localhost:8080/v3/api-docs");
        properties.setPath("/scalar");
        properties.setPageTitle("MyAuth API Documentation");
        properties.setTheme(ScalarTheme.DEEP_SPACE);
        properties.setDarkMode(true);
        properties.setLayout(ScalarLayout.MODERN);

        return properties;
    }
}