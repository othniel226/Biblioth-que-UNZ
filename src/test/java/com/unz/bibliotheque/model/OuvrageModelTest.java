package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutExemplaire;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires du modèle Ouvrage.
 * Vérifie les méthodes de disponibilité.
 */
@DisplayName("Tests modèle Ouvrage")
class OuvrageModelTest {

    private Ouvrage ouvrage;

    @BeforeEach
    void setUp() {
        ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Introduction au Génie Logiciel");
        ouvrage.setAuteur("Ian Sommerville");
        ouvrage.setArchive(false);
        ouvrage.setExemplaires(new ArrayList<>());
    }

    @Test
    @DisplayName("✅ estDisponible() → false si aucun exemplaire")
    void estDisponible_AucunExemplaire() {
        assertThat(ouvrage.estDisponible()).isFalse();
    }

    @Test
    @DisplayName("✅ estDisponible() → true si exemplaire DISPONIBLE")
    void estDisponible_AvecExemplaireDisponible() {
        Exemplaire ex = new Exemplaire();
        ex.setStatut(StatutExemplaire.DISPONIBLE);
        ex.setOuvrage(ouvrage);
        ouvrage.getExemplaires().add(ex);
        assertThat(ouvrage.estDisponible()).isTrue();
    }

    @Test
    @DisplayName("✅ estDisponible() → false si tous EMPRUNTES")
    void estDisponible_TousEmpruntes() {
        Exemplaire ex = new Exemplaire();
        ex.setStatut(StatutExemplaire.EMPRUNTE);
        ex.setOuvrage(ouvrage);
        ouvrage.getExemplaires().add(ex);
        assertThat(ouvrage.estDisponible()).isFalse();
    }

    @Test
    @DisplayName("✅ getNombreExemplairesDisponibles() → compte correct")
    void getNombreExemplairesDisponibles_CompteCorrect() {
        Exemplaire ex1 = new Exemplaire();
        ex1.setStatut(StatutExemplaire.DISPONIBLE);
        Exemplaire ex2 = new Exemplaire();
        ex2.setStatut(StatutExemplaire.EMPRUNTE);
        Exemplaire ex3 = new Exemplaire();
        ex3.setStatut(StatutExemplaire.DISPONIBLE);
        ouvrage.getExemplaires().addAll(List.of(ex1, ex2, ex3));
        assertThat(ouvrage.getNombreExemplairesDisponibles()).isEqualTo(2);
    }

    @Test
    @DisplayName("✅ getPremierExemplaireDisponible() → retourne le premier")
    void getPremierExemplaireDisponible_RetournePremier() {
        Exemplaire ex1 = new Exemplaire();
        ex1.setCodeBarres("EX-001");
        ex1.setStatut(StatutExemplaire.EMPRUNTE);
        Exemplaire ex2 = new Exemplaire();
        ex2.setCodeBarres("EX-002");
        ex2.setStatut(StatutExemplaire.DISPONIBLE);
        ouvrage.getExemplaires().addAll(List.of(ex1, ex2));
        assertThat(ouvrage.getPremierExemplaireDisponible()).isEqualTo(ex2);
    }

    @Test
    @DisplayName("✅ getPremierExemplaireDisponible() → null si aucun dispo")
    void getPremierExemplaireDisponible_NullSiAucun() {
        assertThat(ouvrage.getPremierExemplaireDisponible()).isNull();
    }

    @Test
    @DisplayName("✅ archive → false par défaut")
    void archive_FalseParDefaut() {
        Ouvrage o = new Ouvrage();
        assertThat(o.getArchive()).isFalse();
    }

    @Test
    @DisplayName("✅ Titre et auteur accessibles")
    void titreEtAuteur_Accessibles() {
        assertThat(ouvrage.getTitre()).isEqualTo("Introduction au Génie Logiciel");
        assertThat(ouvrage.getAuteur()).isEqualTo("Ian Sommerville");
    }
}
