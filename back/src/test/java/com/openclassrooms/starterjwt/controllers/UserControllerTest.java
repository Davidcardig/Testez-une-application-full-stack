package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController - Tests unitaires")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setAdmin(false);
    }

    @Test
    @DisplayName("findById - Devrait retourner un utilisateur existant")
    void testFindById_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        ResponseEntity<?> response = userController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(UserDto.class);

        verify(userService, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    @DisplayName("findById - Devrait retourner 404 si l'utilisateur n'existe pas")
    void testFindById_NotFound() {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userService, times(1)).findById(1L);
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    @DisplayName("findById - Devrait retourner 400 si l'id n'est pas valide")
    void testFindById_InvalidId() {
        // Act
        ResponseEntity<?> response = userController.findById("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("delete - Devrait supprimer un utilisateur avec succès")
    void testDelete_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(user);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doNothing().when(userService).delete(1L);

        // Act
        ResponseEntity<?> response = userController.save("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("delete - Devrait retourner 404 si l'utilisateur n'existe pas")
    void testDelete_NotFound() {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.save("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userService, times(1)).findById(1L);
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("delete - Devrait retourner 401 si l'utilisateur n'est pas autorisé")
    void testDelete_Unauthorized() {
        // Arrange
        when(userService.findById(1L)).thenReturn(user);
        when(userDetails.getUsername()).thenReturn("another@example.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<?> response = userController.save("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService, times(1)).findById(1L);
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("delete - Devrait retourner 400 si l'id n'est pas valide")
    void testDelete_InvalidId() {
        // Act
        ResponseEntity<?> response = userController.save("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userService, never()).findById(anyLong());
    }
}
