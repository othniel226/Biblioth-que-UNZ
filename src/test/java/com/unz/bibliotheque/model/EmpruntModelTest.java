package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.model.enums.StatutExemplaire;
import com.unz.bibliotheque.model.enums.Role;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires du modèle Emprunt.
 * Vérifie les règles métier encapsulées dans l'entité.
 */
@DisplayName("Tests modèle Emprunt")
class EmpruntModelTest {

    private Emprunt emprunt;
    private Etudiant etudiant;
    private Exemplaire exemplaire;
    private Ouvrage ouvrage;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setPrenom("Moussa");
        etudiant.setNom("Ouédraogo");
        etudiant.setEmail("moussa@test.bf");
        etudiant.setMotDePasse("hash");
        etudiant.setRole(Role.ETUDIANT);
        etudiant.setActif(true);
        etudiant.setMatricule("2023INF001");
        etudiant.setFiliere("Informatique");
        etudiant.setNiveau("L3");

        ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Design Patterns");
        ouvrage.setAuteur("GoF");
        ouvrage.setArchive(false);

        exemplaire = new Exemplaire();
        exemplaire.setId(1L);
        exemplaire.setCodeBarres("EX-001-A");
        exemplaire.setStatut(StatutExemplaire.EMPRUNTE);
        exemplaire.setOuvrage(ouvrage);

        emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setExemplaire(exemplaire);
        emprunt.setDateEmprunt(LocalDateTime.now());
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setProlonge(false);
    }

    @Test
    @DisplayName("✅ estEnRetard() → false si date future")
    void estEnRetard_DateFuture() {
        emprunt.setDateRetourPrevu(LocalDate.now().plusDays(5));
        assertThat(emprunt.estEnRetard()).isFalse();
    }

    @Test
    @DisplayName("✅ estEnRetard() → true si date passée")
    void estEnRetard_DatePassee() {
        emprunt.setDateRetourPrevu(LocalDate.now().minusDays(3));
        assertThat(emprunt.estEnRetard()).isTrue();
    }

    @Test
    @DisplayName("✅ estEnRetard() → false si déjà rendu")
    void estEnRetard_DejaRendu() {
        emprunt.setDateRetourPrevu(LocalDate.now().minusDays(5));
        emprunt.setStatut(StatutEmprunt.RENDU);
        assertThat(emprunt.estEnRetard()).isFalse();
    }

    @Test
    @DisplayName("✅ getNombreJoursRetard() → 0 si pas en retard")
    void getNombreJoursRetard_PasEnRetard() {
        emprunt.setDateRetourPrevu(LocalDate.now().plusDays(5));
        assertThat(emprunt.getNombreJoursRetard()).isEqualTo(0);
    }

    @Test
    @DisplayName("✅ getNombreJoursRetard() → nombre correct si en retard")
    void getNombreJoursRetard_EnRetard() {
        emprunt.setDateRetourPrevu(LocalDate.now().minusDays(5));
        assertThat(emprunt.getNombreJoursRetard()).isEqualTo(5);
    }

    @Test
    @DisplayName("✅ prolonge → false par défaut")
    void prolonge_FalseParDefaut() {
        assertThat(emprunt.getProlonge()).isFalse();
    }

    @Test
    @DisplayName("✅ statut → EN_COURS par défaut")
    void statut_EnCoursParDefaut() {
        Emprunt nouvelEmprunt = new Emprunt();
        assertThat(nouvelEmprunt.getStatut()).isEqualTo(StatutEmprunt.EN_COURS);
    }

    @Test
    @DisplayName("✅ getNomComplet() étudiant correct")
    void nomComplet_Etudiant() {
        assertThat(etudiant.getNomComplet()).isEqualTo("Moussa Ouédraogo");
    }
}
