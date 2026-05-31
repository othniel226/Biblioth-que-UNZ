package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Exemplaire;
import com.unz.bibliotheque.model.enums.StatutExemplaire;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour l entite Exemplaire.
 *
 * IMPORTANT : La methode findByIdWithLock utilise un verrou pessimiste
 * (PESSIMISTIC_WRITE) pour eviter les emprunts simultanes du meme exemplaire.
 * Design Pattern : Verrou pessimiste (Pessimistic Locking).
 */
@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {

    /**
     * Recherche un exemplaire par son code-barres.
     *
     * @param codeBarres code-barres de l exemplaire
     * @return Optional contenant l exemplaire, ou vide si introuvable
     */
    Optional<Exemplaire> findByCodeBarres(String codeBarres);

    /**
     * Recherche un exemplaire par son identifiant avec un verrou pessimiste.
     *
     * Le verrou PESSIMISTIC_WRITE empeche d autres transactions de modifier
     * cet exemplaire tant que la transaction en cours n est pas terminee.
     * Cela garantit qu un meme exemplaire ne peut pas etre emprunte deux fois
     * simultanement (protection contre les race conditions).
     *
     * @param id identifiant de l exemplaire
     * @return Optional contenant l exemplaire verrouille
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Exemplaire e WHERE e.id = :id")
    Optional<Exemplaire> findByIdWithLock(@Param("id") Long id);

    /**
     * Compte les exemplaires par statut.
     * @param statut statut de l exemplaire
     * @return nombre d exemplaires ayant ce statut
     */
    long countByStatut(StatutExemplaire statut);

    // ══════════════════════════════════════════════════════
    // NOUVELLE MÉTHODE — BibliothécaireController
    // ══════════════════════════════════════════════════════

    /**
     * Liste tous les exemplaires ayant un statut donné.
     *
     * @param statut statut recherché
     * @return liste des exemplaires
     */
    List<Exemplaire> findByStatut(StatutExemplaire statut);
}
