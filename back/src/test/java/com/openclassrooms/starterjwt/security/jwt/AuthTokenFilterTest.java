package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenFilter - Tests unitaires")
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();
    }

    @Test
    @DisplayName("Devrait authentifier un utilisateur avec un token JWT valide")
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("validToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validToken")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
        verify(jwtUtils, times(1)).validateJwtToken("validToken");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("validToken");
        verify(userDetailsService, times(1)).loadUserByUsername("test@example.com");
    }

    @Test
    @DisplayName("Devrait ne pas authentifier avec un token invalide")
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("invalidToken")).thenReturn(false);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, times(1)).validateJwtToken("invalidToken");
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Devrait ne pas authentifier sans header Authorization")
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Devrait ne pas authentifier avec un header Authorization sans Bearer")
    void testDoFilterInternal_AuthorizationHeaderWithoutBearer() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic someToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("Devrait gérer une exception lors de l'authentification")
    void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("validToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validToken")).thenThrow(new RuntimeException("Token parsing error"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtils, times(1)).validateJwtToken("validToken");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("validToken");
    }

    @Test
    @DisplayName("Devrait extraire correctement le token JWT du header")
    void testDoFilterInternal_ParseJwtFromHeader() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer Token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("Token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("Token")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils, times(1)).validateJwtToken("Token");
        verify(jwtUtils, times(1)).getUserNameFromJwtToken("Token");
    }

    @Test
    @DisplayName("Devrait continuer la chaîne de filtres")
    void testDoFilterInternal_FilterChainContinues() throws ServletException, IOException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = spy(new MockFilterChain());

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
