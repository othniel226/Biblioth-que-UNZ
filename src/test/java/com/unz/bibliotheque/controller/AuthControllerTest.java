package com.unz.bibliotheque.controller;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour AuthController.
 * Teste directement les méthodes sans Spring Security.
 */
@DisplayName("Tests AuthController")
class AuthControllerTest {

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController();
    }

    @Test
    @DisplayName("✅ login() → retourne le template auth/login")
    void login_doitRetournerTemplateLogin() {
        String vue = authController.login();
        assertThat(vue).isEqualTo("auth/login");
    }

    @Test
    @DisplayName("✅ logoutSuccess() → redirige vers login avec logout=true")
    void logoutSuccess_doitRedirigerVersLogin() {
        String vue = authController.logoutSuccess();
        assertThat(vue).isEqualTo("redirect:/auth/login?logout=true");
    }
}
