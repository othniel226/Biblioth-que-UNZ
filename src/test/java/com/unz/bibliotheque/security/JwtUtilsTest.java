package com.unz.bibliotheque.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private UserDetails userDetails;
    private static final String SECRET = "monSecretDeTestPourJwt2026BibliothequeUNZ";
    private static final int EXPIRATION = 86400000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION);

        userDetails = User.builder()
            .username("test@unz.bf")
            .password("password")
            .roles("ETUDIANT")
            .build();
    }

    @Test
    void generateToken_avecUserDetails_doitRetournerTokenNonVide() {
        String token = jwtUtils.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void getEmailFromToken_avecTokenValide_doitRetournerEmail() {
        String token = jwtUtils.generateToken(userDetails);
        String email = jwtUtils.getEmailFromToken(token);
        assertEquals("test@unz.bf", email);
    }

    @Test
    void isTokenValid_avecTokenValide_doitRetournerTrue() {
        String token = jwtUtils.generateToken(userDetails);
        boolean valide = jwtUtils.isTokenValid(token, userDetails);
        assertTrue(valide);
    }

    @Test
    void isTokenValid_avecTokenModifie_doitRetournerFalse() {
        String token = jwtUtils.generateToken(userDetails);
        String tokenModifie = token + "X";
        boolean valide = jwtUtils.isTokenValid(tokenModifie, userDetails);
        assertFalse(valide);
    }

    @Test
    void isTokenValid_avecMauvaisUtilisateur_doitRetournerFalse() {
        String token = jwtUtils.generateToken(userDetails);
        UserDetails autreUser = User.builder()
            .username("autre@unz.bf")
            .password("password")
            .roles("ETUDIANT")
            .build();
        boolean valide = jwtUtils.isTokenValid(token, autreUser);
        assertFalse(valide);
    }

    @Test
    void generateToken_deuxAppels_doitRetournerTokensDifferents() {
        // ACT : deux tokens avec des dates d'émission différentes
        String token1 = jwtUtils.generateToken(userDetails, new Date(1000));
        String token2 = jwtUtils.generateToken(userDetails, new Date(2000));

        // ASSERT
        assertNotEquals(token1, token2);
    }
}
