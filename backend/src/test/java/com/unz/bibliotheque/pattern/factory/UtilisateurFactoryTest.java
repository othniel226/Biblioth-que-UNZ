package com.unz.bibliotheque.pattern.factory;

import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests pour le Factory Pattern — UtilisateurFactory
 * Basés sur le code source réel
 */
@DisplayName("Factory Pattern — UtilisateurFactory")
class UtilisateurFactoryTest {

    // ══════════════════════════════════════════════════════
    // CRÉATION PAR RÔLE
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("✅ creerUtilisateur(ETUDIANT) retourne une instance d'Etudiant")
    void creer_ETUDIANT_retourneEtudiant() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "Ouedraogo", "Moussa", "moussa@etud.unz.bf");

        assertThat(u).isInstanceOf(Etudiant.class);
    }

    @Test
    @DisplayName("✅ creerUtilisateur(BIBLIOTHECAIRE) retourne une instance de Bibliothecaire")
    void creer_BIBLIOTHECAIRE_retourneBibliothecaire() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.BIBLIOTHECAIRE, "Kabore", "Alice", "alice@bibliotheque-unz.bf");

        assertThat(u).isInstanceOf(Bibliothecaire.class);
    }

    @Test
    @DisplayName("✅ creerUtilisateur(ADMINISTRATEUR) retourne une instance d'Administrateur")
    void creer_ADMINISTRATEUR_retourneAdministrateur() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ADMINISTRATEUR, "Admin", "Super", "admin@bibliotheque-unz.bf");

        assertThat(u).isInstanceOf(Administrateur.class);
    }

    // ══════════════════════════════════════════════════════
    // CHAMPS CORRECTEMENT AFFECTÉS
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("✅ Les champs nom, prénom, email sont correctement affectés")
    void creer_champsCorrectementAffectes() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "Sawadogo", "Boureima", "boureima@etud.unz.bf");

        assertThat(u.getNom()).isEqualTo("Sawadogo");
        assertThat(u.getPrenom()).isEqualTo("Boureima");
        assertThat(u.getEmail()).isEqualTo("boureima@etud.unz.bf");
    }

    @Test
    @DisplayName("✅ Le rôle ETUDIANT est correctement affecté")
    void creer_ETUDIANT_roleCorrect() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "Barry", "Aliou", "aliou@etud.unz.bf");

        assertThat(u.getRole()).isEqualTo(Role.ETUDIANT);
    }

    @Test
    @DisplayName("✅ Le rôle BIBLIOTHECAIRE est correctement affecté")
    void creer_BIBLIOTHECAIRE_roleCorrect() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.BIBLIOTHECAIRE, "Kabore", "Alice", "alice@bibliotheque-unz.bf");

        assertThat(u.getRole()).isEqualTo(Role.BIBLIOTHECAIRE);
    }

    @Test
    @DisplayName("✅ Le rôle ADMINISTRATEUR est correctement affecté")
    void creer_ADMINISTRATEUR_roleCorrect() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ADMINISTRATEUR, "Admin", "Super", "admin@bibliotheque-unz.bf");

        assertThat(u.getRole()).isEqualTo(Role.ADMINISTRATEUR);
    }

    @Test
    @DisplayName("✅ Le compte est actif par défaut à la création")
    void creer_compteActifParDefaut() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "Coulibaly", "Kadiatou", "kadiatou@etud.unz.bf");

        assertThat(u.getActif()).isTrue();
    }

    // ══════════════════════════════════════════════════════
    // POLYMORPHISME
    // ══════════════════════════════════════════════════════

    @ParameterizedTest(name = "Rôle {0} → Utilisateur créé avec succès")
    @EnumSource(Role.class)
    @DisplayName("✅ Tous les rôles créent un utilisateur non null")
    void creer_tousRoles_retourneNonNull(Role role) {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                role, "Test", "Test", "test@unz.bf");

        assertThat(u).isNotNull();
        assertThat(u.getActif()).isTrue();
        assertThat(u.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("✅ Etudiant est bien une instance de Utilisateur (héritage)")
    void etudiant_estInstanceDeUtilisateur() {
        Utilisateur u = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "X", "Y", "xy@etud.unz.bf");

        assertThat(u).isInstanceOf(Utilisateur.class);
        assertThat(u).isInstanceOf(Etudiant.class);
    }

    @Test
    @DisplayName("✅ Deux appels créent deux instances distinctes")
    void creer_deuxAppels_deuxInstancesDistinctes() {
        Utilisateur u1 = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "A", "B", "ab@etud.unz.bf");
        Utilisateur u2 = UtilisateurFactory.creerUtilisateur(
                Role.ETUDIANT, "C", "D", "cd@etud.unz.bf");

        assertThat(u1).isNotSameAs(u2);
        assertThat(u1.getEmail()).isNotEqualTo(u2.getEmail());
    }
}
