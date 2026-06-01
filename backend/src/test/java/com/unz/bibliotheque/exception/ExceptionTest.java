package com.unz.bibliotheque.exception;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires des exceptions métier.
 */
@DisplayName("Tests Exceptions Métier")
class ExceptionTest {

    @Test
    @DisplayName("✅ BusinessException contient le bon message")
    void businessException_Message() {
        BusinessException ex = new BusinessException("Quota d'emprunts dépassé.");
        assertThat(ex.getMessage()).isEqualTo("Quota d'emprunts dépassé.");
    }

    @Test
    @DisplayName("✅ ResourceNotFoundException contient le bon message")
    void resourceNotFoundException_Message() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Ouvrage introuvable.");
        assertThat(ex.getMessage()).isEqualTo("Ouvrage introuvable.");
    }

    @Test
    @DisplayName("✅ UnauthorizedException contient le bon message")
    void unauthorizedException_Message() {
        UnauthorizedException ex = new UnauthorizedException("Accès refusé.");
        assertThat(ex.getMessage()).isEqualTo("Accès refusé.");
    }

    @Test
    @DisplayName("✅ BusinessException est une RuntimeException")
    void businessException_EstRuntimeException() {
        assertThat(new BusinessException("test"))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("✅ ResourceNotFoundException est une RuntimeException")
    void resourceNotFoundException_EstRuntimeException() {
        assertThat(new ResourceNotFoundException("test"))
            .isInstanceOf(RuntimeException.class);
    }
}
