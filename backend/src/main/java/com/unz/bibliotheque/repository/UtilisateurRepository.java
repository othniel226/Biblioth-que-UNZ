package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Utilisateur;
import com.unz.bibliotheque.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    // ── BibliothecaireController : liste des étudiants actifs ────────
    @Query("SELECT e FROM Etudiant e WHERE e.actif = true ORDER BY e.nom")
    List<Etudiant> findAllEtudiants();
}
