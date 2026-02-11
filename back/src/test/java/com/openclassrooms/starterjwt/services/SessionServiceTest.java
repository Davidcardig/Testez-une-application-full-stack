package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService - Tests unitaires")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password123")
                .admin(false)
                .build();

        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Test session")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Devrait créer une session avec succès")
    void testCreate_Success() {
        // Given
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // When
        Session result = sessionService.create(session);

        // Then
        verify(sessionRepository, times(1)).save(session);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Yoga Session");
    }

    @Test
    @DisplayName("Devrait supprimer une session par son ID")
    void testDelete_Success() {
        // Given
        Long sessionId = 1L;
        doNothing().when(sessionRepository).deleteById(sessionId);

        // When
        sessionService.delete(sessionId);

        // Then
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    @DisplayName("Devrait retourner toutes les sessions")
    void testFindAll_Success() {
        // Given
        Session session2 = Session.builder()
                .id(2L)
                .name("Meditation Session")
                .date(new Date())
                .description("Another session")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        List<Session> sessions = Arrays.asList(session, session2);
        when(sessionRepository.findAll()).thenReturn(sessions);

        // When
        List<Session> result = sessionService.findAll();

        // Then
        verify(sessionRepository, times(1)).findAll();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).contains(session, session2);
    }

    @Test
    @DisplayName("Devrait retourner une liste vide quand aucune session n'existe")
    void testFindAll_EmptyList() {
        // Given
        when(sessionRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<Session> result = sessionService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait retourner une session par son ID")
    void testGetById_Success() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // When
        Session result = sessionService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Yoga Session");
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait retourner null quand la session n'existe pas")
    void testGetById_NotFound() {
        // Given
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Session result = sessionService.getById(999L);

        // Then
        assertThat(result).isNull();
        verify(sessionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Devrait mettre à jour une session avec succès")
    void testUpdate_Success() {
        // Given
        Session updatedSession = Session.builder()
                .name("Updated Yoga Session")
                .date(new Date())
                .description("Updated description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        Session savedSession = Session.builder()
                .id(1L)
                .name("Updated Yoga Session")
                .date(updatedSession.getDate())
                .description("Updated description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

        // When
        Session result = sessionService.update(1L, updatedSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Yoga Session");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Devrait permettre à un utilisateur de participer à une session")
    void testParticipate_Success() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // When
        sessionService.participate(1L, 1L);

        // Then
        verify(sessionRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(sessionRepository, times(1)).save(session);
        assertThat(session.getUsers()).contains(user);
    }

    @Test
    @DisplayName("Devrait lever NotFoundException quand la session n'existe pas pour participer")
    void testParticipate_SessionNotFound() {
        // Given
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sessionService.participate(999L, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(sessionRepository, times(1)).findById(999L);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    @DisplayName("Devrait lever NotFoundException quand l'utilisateur n'existe pas pour participer")
    void testParticipate_UserNotFound() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sessionService.participate(1L, 999L))
                .isInstanceOf(NotFoundException.class);

        verify(sessionRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(999L);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    @DisplayName("Devrait lever BadRequestException quand l'utilisateur participe déjà")
    void testParticipate_UserAlreadyParticipating() {
        // Given
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                .isInstanceOf(BadRequestException.class);

        verify(sessionRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    @DisplayName("Devrait permettre à un utilisateur de ne plus participer à une session")
    void testNoLongerParticipate_Success() {
        // Given
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // When
        sessionService.noLongerParticipate(1L, 1L);

        // Then
        verify(sessionRepository, times(1)).findById(1L);
        verify(sessionRepository, times(1)).save(session);
        assertThat(session.getUsers()).doesNotContain(user);
    }

    @Test
    @DisplayName("Devrait lever NotFoundException quand la session n'existe pas pour ne plus participer")
    void testNoLongerParticipate_SessionNotFound() {
        // Given
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sessionService.noLongerParticipate(999L, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(sessionRepository, times(1)).findById(999L);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    @DisplayName("Devrait lever BadRequestException quand l'utilisateur ne participe pas")
    void testNoLongerParticipate_UserNotParticipating() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // When & Then
        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                .isInstanceOf(BadRequestException.class);

        verify(sessionRepository, times(1)).findById(1L);
        verify(sessionRepository, never()).save(any(Session.class));
    }
}
