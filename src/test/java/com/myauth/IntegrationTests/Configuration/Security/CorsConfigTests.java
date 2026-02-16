package com.myauth.IntegrationTests.Configuration.Security;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CORS Config Tests")
class CorsConfigTests {
    @Autowired
    private MockMvc mockMvc;

    private final String TRUSTED_ORIGIN = "http://localhost:3000";
    private final String EVIL_ORIGIN = "http://evil-hacker.com";

    @Test
    @DisplayName("Should allow Preflight (OPTIONS) from trusted origin")
    void shouldAllowPreflightFromTrustedOrigin() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", TRUSTED_ORIGIN)
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(header().string("Access-Control-Allow-Origin", TRUSTED_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"));
    }

    @Test
    @DisplayName("Should allow Simple Request (GET) from trusted origin")
    void shouldAllowSimpleRequestFromTrustedOrigin() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                .header("Origin", TRUSTED_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Origin", TRUSTED_ORIGIN));
    }

    @Test
    @DisplayName("Should BLOCK Preflight from unknown origin")
    void shouldBlockPreflightFromUnknownOrigin() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", EVIL_ORIGIN)
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should NOT add CORS headers for unknown origin")
    void shouldNotAddHeadersForUnknownOrigin() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                .header("Origin", EVIL_ORIGIN))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Should allow custom headers (Device-Id)")
    void shouldAllowCustomHeaders() throws Exception {
        String requestedHeaders = "Device-Id, Authorization";

        mockMvc.perform(options("/api/auth/login")
                .header("Origin", TRUSTED_ORIGIN)
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", requestedHeaders))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Device-Id")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Authorization")));
    }

    @Test
    @DisplayName("Should NOT allow credentials (Cookies)")
    void shouldNotAllowCredentials() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", TRUSTED_ORIGIN)
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }
}