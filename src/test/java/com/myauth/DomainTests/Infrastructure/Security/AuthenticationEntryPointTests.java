package com.myauth.DomainTests.Infrastructure.Security;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import com.myauth.infrastructure.security.CustomAuthenticationEntryPoint;

@DisplayName("Authentication Entry Point Tests")
class AuthenticationEntryPointTests {
    @Test
    @DisplayName("Should return 401 Unauthorized and correct JSON body")
    void shouldReturn401AndJson() throws IOException, jakarta.servlet.ServletException {
        // Arrange
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Msg") {};

        // Act
        entryPoint.commence(request, response, authException);

        // Assert
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).contains("application/json");
        assertThat(response.getContentAsString()).contains("\"error\":\"Unauthorized\"");
    }
}