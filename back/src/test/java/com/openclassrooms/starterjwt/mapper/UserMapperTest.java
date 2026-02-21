package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper - Tests unitaires")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Devrait convertir User en UserDto")
    void testToDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        UserDto userDto = userMapper.toDto(user);

        // Then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
        assertThat(userDto.getFirstName()).isEqualTo("John");
        assertThat(userDto.getLastName()).isEqualTo("Doe");
        assertThat(userDto.isAdmin()).isFalse();
        assertThat(userDto.getCreatedAt()).isEqualTo(now);
        assertThat(userDto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait convertir UserDto en User")
    void testToEntity() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UserDto userDto = new UserDto(1L, "test@example.com", "Doe", "John", false, "password123", now, now);

        // When
        User user = userMapper.toEntity(userDto);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait convertir une liste de Users en liste de UserDtos")
    void testToDtoList() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user1 = User.builder()
                .id(1L)
                .email("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("test2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password456")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<User> users = Arrays.asList(user1, user2);

        // When
        List<UserDto> userDtos = userMapper.toDto(users);

        // Then
        assertThat(userDtos).isNotNull();
        assertThat(userDtos).hasSize(2);
        assertThat(userDtos.get(0).getId()).isEqualTo(1L);
        assertThat(userDtos.get(0).getEmail()).isEqualTo("test1@example.com");
        assertThat(userDtos.get(0).isAdmin()).isFalse();
        assertThat(userDtos.get(1).getId()).isEqualTo(2L);
        assertThat(userDtos.get(1).getEmail()).isEqualTo("test2@example.com");
        assertThat(userDtos.get(1).isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait convertir une liste de UserDtos en liste de Users")
    void testToEntityList() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UserDto userDto1 = new UserDto(1L, "test1@example.com", "Doe", "John", false, "password123", now, now);
        UserDto userDto2 = new UserDto(2L, "test2@example.com", "Smith", "Jane", true, "password456", now, now);
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);

        // When
        List<User> users = userMapper.toEntity(userDtos);

        // Then
        assertThat(users).isNotNull();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(0).getEmail()).isEqualTo("test1@example.com");
        assertThat(users.get(0).isAdmin()).isFalse();
        assertThat(users.get(1).getId()).isEqualTo(2L);
        assertThat(users.get(1).getEmail()).isEqualTo("test2@example.com");
        assertThat(users.get(1).isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner null si User est null")
    void testToDto_NullUser() {
        // When
        UserDto userDto = userMapper.toDto((User) null);

        // Then
        assertThat(userDto).isNull();
    }

    @Test
    @DisplayName("Devrait retourner null si UserDto est null")
    void testToEntity_NullDto() {
        // When
        User user = userMapper.toEntity((UserDto) null);

        // Then
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("Devrait convertir un utilisateur admin")
    void testToDto_AdminUser() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminPassword")
                .admin(true)
                .build();

        // When
        UserDto userDto = userMapper.toDto(user);

        // Then
        assertThat(userDto).isNotNull();
        assertThat(userDto.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Devrait convertir une liste vide de Users en liste vide de UserDtos")
    void testToDtoList_EmptyList() {
        // Given
        List<User> users = Arrays.asList();

        // When
        List<UserDto> userDtos = userMapper.toDto(users);

        // Then
        assertThat(userDtos).isNotNull();
        assertThat(userDtos).isEmpty();
    }

    @Test
    @DisplayName("Devrait convertir une liste vide de UserDtos en liste vide de Users")
    void testToEntityList_EmptyList() {
        // Given
        List<UserDto> userDtos = Arrays.asList();

        // When
        List<User> users = userMapper.toEntity(userDtos);

        // Then
        assertThat(users).isNotNull();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("Devrait retourner null pour une liste nulle de Users")
    void testToDtoList_NullList() {
        // When
        List<UserDto> userDtos = userMapper.toDto((List<User>) null);

        // Then
        assertThat(userDtos).isNull();
    }

    @Test
    @DisplayName("Devrait retourner null pour une liste nulle de UserDtos")
    void testToEntityList_NullList() {
        // When
        List<User> users = userMapper.toEntity((List<UserDto>) null);

        // Then
        assertThat(users).isNull();
    }
}
