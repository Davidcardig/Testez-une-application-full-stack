package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NotFoundException - Tests unitaires")
class NotFoundExceptionTest {

    @Test
    @DisplayName("Devrait créer une NotFoundException")
    void testNotFoundExceptionCreation() {
        // When
        NotFoundException exception = new NotFoundException();

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Devrait lever une NotFoundException")
    void testNotFoundExceptionThrown() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new NotFoundException();
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Devrait avoir l'annotation @ResponseStatus avec NOT_FOUND")
    void testNotFoundExceptionAnnotation() {
        // Given
        Class<NotFoundException> exceptionClass = NotFoundException.class;

        // When
        ResponseStatus annotation = exceptionClass.getAnnotation(ResponseStatus.class);

        // Then
        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Devrait être attrapée comme RuntimeException")
    void testNotFoundExceptionAsRuntimeException() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new NotFoundException();
        }).isInstanceOf(RuntimeException.class);
    }
}
