package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController - Tests unitaires")
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Une session de yoga relaxante")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Une session de yoga relaxante");
        sessionDto.setTeacher_id(1L);
    }

    @Test
    @DisplayName("findById - Devrait retourner une session existante")
    void testFindById_Success() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SessionDto.class);
        assertThat(((SessionDto) response.getBody()).getId()).isEqualTo(1L);

        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    @DisplayName("findById - Devrait retourner 404 si la session n'existe pas")
    void testFindById_NotFound() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, never()).toDto(any(Session.class));
    }

    @Test
    @DisplayName("findById - Devrait retourner 400 si l'id n'est pas valide")
    void testFindById_InvalidId() {
        // Act
        ResponseEntity<?> response = sessionController.findById("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).getById(anyLong());
    }

    @Test
    @DisplayName("findAll - Devrait retourner toutes les sessions")
    void testFindAll_Success() {
        // Arrange
        Session session2 = Session.builder()
                .id(2L)
                .name("Yoga Session 2")
                .date(new Date())
                .description("Une autre session")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        List<Session> sessions = Arrays.asList(session, session2);
        List<SessionDto> sessionDtos = Arrays.asList(sessionDto, new SessionDto());

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(anyList())).thenReturn(sessionDtos);

        // Act
        ResponseEntity<?> response = sessionController.findAll();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
        assertThat(((List<?>) response.getBody())).hasSize(2);

        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(anyList());
    }

    @Test
    @DisplayName("create - Devrait créer une nouvelle session")
    void testCreate_Success() {
        // Arrange
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.create(sessionDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SessionDto.class);

        verify(sessionMapper, times(1)).toEntity(sessionDto);
        verify(sessionService, times(1)).create(session);
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    @DisplayName("update - Devrait mettre à jour une session existante")
    void testUpdate_Success() {
        // Arrange
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SessionDto.class);

        verify(sessionService, times(1)).update(1L, session);
        verify(sessionMapper, times(1)).toDto(any(Session.class));
    }

    @Test
    @DisplayName("update - Devrait retourner 400 si l'id n'est pas valide")
    void testUpdate_InvalidId() {
        // Act
        ResponseEntity<?> response = sessionController.update("invalid", sessionDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("delete - Devrait supprimer une session existante")
    void testDelete_Success() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session);
        doNothing().when(sessionService).delete(1L);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("delete - Devrait retourner 404 si la session n'existe pas")
    void testDelete_NotFound() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("delete - Devrait retourner 400 si l'id n'est pas valide")
    void testDelete_InvalidId() {
        // Act
        ResponseEntity<?> response = sessionController.save("invalid");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).getById(anyLong());
    }

    @Test
    @DisplayName("participate - Devrait ajouter un utilisateur à une session")
    void testParticipate_Success() {
        // Arrange
        doNothing().when(sessionService).participate(1L, 2L);

        // Act
        ResponseEntity<?> response = sessionController.participate("1", "2");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).participate(1L, 2L);
    }

    @Test
    @DisplayName("participate - Devrait retourner 400 si l'id n'est pas valide")
    void testParticipate_InvalidId() {
        // Act
        ResponseEntity<?> response = sessionController.participate("invalid", "2");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).participate(anyLong(), anyLong());
    }

    @Test
    @DisplayName("noLongerParticipate - Devrait retirer un utilisateur d'une session")
    void testNoLongerParticipate_Success() {
        // Arrange
        doNothing().when(sessionService).noLongerParticipate(1L, 2L);

        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
    }

    @Test
    @DisplayName("noLongerParticipate - Devrait retourner 400 si l'id n'est pas valide")
    void testNoLongerParticipate_InvalidId() {
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("invalid", "2");


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}
