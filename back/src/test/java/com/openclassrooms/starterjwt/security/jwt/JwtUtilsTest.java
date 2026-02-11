package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils - Tests unitaires")
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private String jwtSecret = "testSecretKeyForJwtTokenGenerationAndValidation";
    private int jwtExpirationMs = 86400000; // 1 day

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);

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
    @DisplayName("Devrait générer un token JWT valide")
    void testGenerateJwtToken() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Devrait extraire le username d'un token JWT valide")
    void testGetUserNameFromJwtToken() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Devrait valider un token JWT valide")
    void testValidateJwtToken_ValidToken() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter un token JWT invalide")
    void testValidateJwtToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Devrait rejeter un token JWT avec une signature invalide")
    void testValidateJwtToken_InvalidSignature() {
        // Given
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        // When
        boolean isValid = jwtUtils.validateJwtToken(tokenWithWrongSignature);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Devrait rejeter un token JWT expiré")
    void testValidateJwtToken_ExpiredToken() {
        // Given
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000000))
                .setExpiration(new Date(System.currentTimeMillis() - 500000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        // When
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Devrait rejeter un token JWT vide")
    void testValidateJwtToken_EmptyToken() {
        // When
        boolean isValid = jwtUtils.validateJwtToken("");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Devrait générer des tokens différents pour différents utilisateurs")
    void testGenerateJwtToken_DifferentUsers() {
        // Given
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("user1@example.com")
                .firstName("User")
                .lastName("One")
                .password("password1")
                .admin(false)
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(2L)
                .username("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .password("password2")
                .admin(false)
                .build();

        when(authentication.getPrincipal()).thenReturn(user1).thenReturn(user2);

        // When
        String token1 = jwtUtils.generateJwtToken(authentication);
        String token2 = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(token1).isNotEqualTo(token2);

        String username1 = jwtUtils.getUserNameFromJwtToken(token1);
        String username2 = jwtUtils.getUserNameFromJwtToken(token2);

        assertThat(username1).isEqualTo("user1@example.com");
        assertThat(username2).isEqualTo("user2@example.com");
    }

    @Test
    @DisplayName("Devrait valider le token et extraire les informations correctes")
    void testTokenWorkflow() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtUtils.generateJwtToken(authentication);
        boolean isValid = jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertThat(isValid).isTrue();
        assertThat(username).isEqualTo("test@example.com");
    }
}
