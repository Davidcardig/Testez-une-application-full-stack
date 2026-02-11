package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Tests unitaires")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Initialisation des objets pour le test unitaire
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("test123");

        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("david");
        signupRequest.setLastName("Cardigos");
        signupRequest.setPassword("test123");

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .admin(false)
                .build();

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("Login - Devrait authentifier un utilisateur avec succès")
    void testAuthenticateUser_Success() {
        // Arrange
        String expectedToken = "jwt-token-123";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getToken()).isEqualTo(expectedToken);
        assertThat(jwtResponse.getId()).isEqualTo(1L);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Login - Devrait authentifier un utilisateur admin avec succès")
    void testAuthenticateUser_AdminSuccess() {
        // Arrange
        String expectedToken = "jwt-token-admin";
        User adminUser = User.builder()
                .id(1L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .admin(true)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(adminUser));

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getToken()).isEqualTo(expectedToken);
        assertThat(jwtResponse.getAdmin()).isTrue();

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Login - Devrait retourner isAdmin false si l'utilisateur n'est pas trouvé")
    void testAuthenticateUser_UserNotFoundInRepository() {
        // Arrange
        String expectedToken = "jwt-token-123";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getToken()).isEqualTo(expectedToken);
        assertThat(jwtResponse.getAdmin()).isFalse();

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Register - Devrait enregistrer un nouvel utilisateur avec succès")
    void testRegisterUser_Success() {
        // Arrange
        String encodedPassword = "encodedPassword123";
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);

        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");

        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("test123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register - Devrait retourner une erreur si l'email existe déjà")
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);

        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");

        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

}
