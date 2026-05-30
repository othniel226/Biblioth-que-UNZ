package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Utilisateur;
import com.unz.bibliotheque.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UtilisateurRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // ============================================================
    // Méthode utilitaire pour créer un Etudiant valide
    // ============================================================
    private Etudiant creerEtudiant(String nom, String prenom, String email, String matricule, Role role) {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setEmail(email);
        etudiant.setMotDePasse("password");
        etudiant.setRole(role);
        etudiant.setActif(true);
        etudiant.setMatricule(matricule);
        etudiant.setNiveau("L3");           // ← OBLIGATOIRE
        etudiant.setFiliere("Informatique"); // ← OBLIGATOIRE
        return etudiant;
    }

    // TEST 1 : findByEmail — utilisateur trouvé
    @Test
    void findByEmail_avecEmailExistant_doitRetournerUtilisateur() {
        Etudiant user = creerEtudiant("Test", "User", "test@unz.bf", "2023INF001", Role.ETUDIANT);
        entityManager.persist(user);
        entityManager.flush();

        Optional<Utilisateur> result = utilisateurRepository.findByEmail("test@unz.bf");

        assertTrue(result.isPresent());
        assertEquals("test@unz.bf", result.get().getEmail());
        assertEquals("Test", result.get().getNom());
    }

    // TEST 2 : findByEmail — utilisateur introuvable
    @Test
    void findByEmail_avecEmailInexistant_doitRetournerVide() {
        Optional<Utilisateur> result = utilisateurRepository.findByEmail("inexistant@unz.bf");

        assertFalse(result.isPresent());
    }

    // TEST 3 : existsByEmail — email existant
    @Test
    void existsByEmail_avecEmailExistant_doitRetournerTrue() {
        Etudiant user = creerEtudiant("Test", "User", "exists@unz.bf", "2023INF002", Role.ETUDIANT);
        entityManager.persist(user);
        entityManager.flush();

        boolean exists = utilisateurRepository.existsByEmail("exists@unz.bf");

        assertTrue(exists);
    }

    // TEST 4 : existsByEmail — email inexistant
    @Test
    void existsByEmail_avecEmailInexistant_doitRetournerFalse() {
        boolean exists = utilisateurRepository.existsByEmail("nope@unz.bf");

        assertFalse(exists);
    }

    // TEST 5 : countByRole — comptage par rôle
    @Test
    void countByRole_avecPlusieursEtudiants_doitRetournerNombreCorrect() {
        Etudiant etu1 = creerEtudiant("Etu1", "Test", "etu1@unz.bf", "2023INF003", Role.ETUDIANT);
        Etudiant etu2 = creerEtudiant("Etu2", "Test", "etu2@unz.bf", "2023INF004", Role.ETUDIANT);
        Etudiant admin = creerEtudiant("Admin", "Test", "admin@unz.bf", "2023INF005", Role.ADMINISTRATEUR);

        entityManager.persist(etu1);
        entityManager.persist(etu2);
        entityManager.persist(admin);
        entityManager.flush();

        long countEtudiants = utilisateurRepository.countByRole(Role.ETUDIANT);
        long countAdmins = utilisateurRepository.countByRole(Role.ADMINISTRATEUR);

        assertEquals(2, countEtudiants);
        assertEquals(1, countAdmins);
    }
}
