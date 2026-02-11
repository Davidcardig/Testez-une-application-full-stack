package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BadRequestException - Tests unitaires")
class BadRequestExceptionTest {

    @Test
    @DisplayName("Devrait créer une BadRequestException")
    void testBadRequestExceptionCreation() {
        // When
        BadRequestException exception = new BadRequestException();

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Devrait lever une BadRequestException")
    void testBadRequestExceptionThrown() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new BadRequestException();
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Devrait avoir l'annotation @ResponseStatus avec BAD_REQUEST")
    void testBadRequestExceptionAnnotation() {
        // Given
        Class<BadRequestException> exceptionClass = BadRequestException.class;

        // When
        ResponseStatus annotation = exceptionClass.getAnnotation(ResponseStatus.class);

        // Then
        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Devrait être attrapée comme RuntimeException")
    void testBadRequestExceptionAsRuntimeException() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new BadRequestException();
        }).isInstanceOf(RuntimeException.class);
    }
}
