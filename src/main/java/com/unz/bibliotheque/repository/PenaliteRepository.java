package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Penalite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

/**
 * Repository JPA pour l'entité Penalite.
 */
@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {

    /**
     * Calcule la somme totale des pénalités impayées d'un étudiant.
     * Utilisé pour vérifier si le compte est bloqué (seuil configurable).
     *
     * @param etudiantId identifiant de l'étudiant
     * @return somme des pénalités impayées en FCFA (0 si aucune)
     */
    @Query("""
        SELECT COALESCE(SUM(p.montant), 0) FROM Penalite p
        WHERE p.emprunt.etudiant.id = :etudiantId
        AND p.payee = false
        """)
    BigDecimal sumMontantImpayeByEtudiantId(@Param("etudiantId") Long etudiantId);

    /**
     * Compte les penalites impayees.
     * @return nombre de penalites non payees
     */
    long countByPayeeFalse();

    // ══════════════════════════════════════════════════════
    // NOUVELLE MÉTHODE — BibliothécaireController
    // ══════════════════════════════════════════════════════

    /**
     * Recherche paginée des pénalités par statut de paiement.
     *
     * @param payee    true = payées, false = impayées
     * @param pageable paramètres de pagination
     * @return page de pénalités
     */
    Page<Penalite> findByPayee(Boolean payee, Pageable pageable);
}
