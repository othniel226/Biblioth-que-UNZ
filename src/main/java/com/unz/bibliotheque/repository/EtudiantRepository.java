package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository JPA pour l'entité Etudiant.
 */
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    /**
     * Recherche un étudiant par son matricule universitaire.
     *
     * @param matricule matricule de l'étudiant (ex: 2023INF001)
     * @return Optional contenant l'étudiant, ou vide si introuvable
     */
    Optional<Etudiant> findByMatricule(String matricule);

    /**
     * Vérifie si un matricule est déjà enregistré.
     *
     * @param matricule matricule à vérifier
     * @return true si le matricule existe déjà
     */
    boolean existsByMatricule(String matricule);

    /**
     * Recherche un étudiant par son adresse email.
     * Requête JPQL sur le champ hérité de Utilisateur.
     *
     * @param email adresse email de l'étudiant
     * @return Optional contenant l'étudiant, ou vide si introuvable
     */
    @Query("SELECT e FROM Etudiant e WHERE e.email = :email")
    Optional<Etudiant> findByEmail(@Param("email") String email);
}
