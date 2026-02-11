package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthEntryPointJwt - Tests unitaires")
class AuthEntryPointJwtTest {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @DisplayName("Devrait retourner une réponse 401 Unauthorized avec le bon format")
    void testCommence_ReturnsUnauthorizedResponse() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException authException = new BadCredentialsException("Bad credentials");

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("401");
        assertThat(response.getContentAsString()).contains("Unauthorized");
        assertThat(response.getContentAsString()).contains("Bad credentials");
        assertThat(response.getContentAsString()).contains("/api/test");
    }

    @Test
    @DisplayName("Devrait gérer différents types d'exceptions d'authentification")
    void testCommence_DifferentExceptionMessages() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/secure");
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException authException = new BadCredentialsException("Invalid token");

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Invalid token");
    }

    @Test
    @DisplayName("Devrait retourner un JSON valide")
    void testCommence_ReturnsValidJson() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException authException = new BadCredentialsException("Access denied");

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        String content = response.getContentAsString();
        assertThat(content).startsWith("{");
        assertThat(content).endsWith("}");
        assertThat(content).contains("\"status\"");
        assertThat(content).contains("\"error\"");
        assertThat(content).contains("\"message\"");
        assertThat(content).contains("\"path\"");
    }

    @Test
    @DisplayName("Devrait gérer un chemin vide")
    void testCommence_EmptyPath() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("");
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException authException = new BadCredentialsException("Unauthorized access");

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"path\":\"\"");
    }
}
