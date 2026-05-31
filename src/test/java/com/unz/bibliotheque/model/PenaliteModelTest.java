package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires du modèle Penalite.
 */
@DisplayName("Tests modèle Pénalité")
class PenaliteModelTest {

    private Penalite penalite;
    private Emprunt emprunt;

    @BeforeEach
    void setUp() {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setMatricule("2023INF001");
        etudiant.setRole(Role.ETUDIANT);
        etudiant.setActif(true);

        emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setDateEmprunt(LocalDateTime.now().minusDays(20));
        emprunt.setDateRetourPrevu(LocalDate.now().minusDays(5));
        emprunt.setStatut(StatutEmprunt.EN_RETARD);

        penalite = new Penalite();
        penalite.setId(1L);
        penalite.setEmprunt(emprunt);
        penalite.setMontant(new BigDecimal("500"));
        penalite.setJoursRetard(5L);
        penalite.setPayee(false);
        penalite.setDateCreation(LocalDateTime.now());
    }

    @Test
    @DisplayName("✅ payee → false par défaut")
    void payee_FalseParDefaut() {
        assertThat(penalite.getPayee()).isFalse();
    }

    @Test
    @DisplayName("✅ montant → 500 FCFA pour 5 jours")
    void montant_500FCFA() {
        assertThat(penalite.getMontant()).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    @DisplayName("✅ marquerPayee() → change payee à true et définit la date")
    void marquerPayee_ChangeStatut() {
        penalite.marquerPayee();
        assertThat(penalite.getPayee()).isTrue();
        assertThat(penalite.getDatePaiement()).isNotNull();
    }

    @Test
    @DisplayName("✅ joursRetard → 5 jours")
    void joursRetard_CinqJours() {
        assertThat(penalite.getJoursRetard()).isEqualTo(5L);
    }

    @Test
    @DisplayName("✅ emprunt → lié correctement")
    void emprunt_LieCorrectement() {
        assertThat(penalite.getEmprunt()).isEqualTo(emprunt);
        assertThat(penalite.getEmprunt().getId()).isEqualTo(1L);
    }
}
