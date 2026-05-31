package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository JPA pour l'entité Emprunt.
 */
@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {

    /**
     * Retourne les emprunts actifs d'un étudiant (EN_COURS, EN_RETARD, PROLONGE).
     *
     * @param etudiantId identifiant de l'étudiant
     * @return liste des emprunts actifs
     */
    @Query("""
        SELECT e FROM Emprunt e
        WHERE e.etudiant.id = :etudiantId
        AND e.statut <> 'RENDU'
        ORDER BY e.dateEmprunt DESC
        """)
    List<Emprunt> findEmpruntsActifsByEtudiant(@Param("etudiantId") Long etudiantId);

    /**
     * Retourne l'historique complet (paginé) des emprunts d'un étudiant.
     *
     * @param etudiantId identifiant de l'étudiant
     * @param pageable   paramètres de pagination
     * @return page d'emprunts triés par date décroissante
     */
    @Query("""
        SELECT e FROM Emprunt e
        WHERE e.etudiant.id = :etudiantId
        ORDER BY e.dateEmprunt DESC
        """)
    Page<Emprunt> findHistoriqueByEtudiant(@Param("etudiantId") Long etudiantId, Pageable pageable);

    /**
     * Retourne tous les emprunts dont la date de retour prévue correspond à une date donnée.
     * Utilisé par le scheduler de rappel J-3.
     *
     * @param date date de retour prévue
     * @return liste des emprunts concernés
     */
    @Query("""
        SELECT e FROM Emprunt e
        WHERE e.dateRetourPrevu = :date
        AND e.statut IN ('EN_COURS', 'PROLONGE')
        """)
    List<Emprunt> findByDateRetourPrevuAndStatutIn(@Param("date") LocalDate date);

    /**
     * Retourne tous les emprunts en retard (date dépassée, pas encore rendus).
     * Utilisé par le scheduler de vérification des retards.
     *
     * @param today date du jour
     * @return liste des emprunts en retard
     */
    @Query("""
        SELECT e FROM Emprunt e
        WHERE e.dateRetourPrevu < :today
        AND e.statut IN ('EN_COURS', 'PROLONGE')
        """)
    List<Emprunt> findEmpruntsEnRetard(@Param("today") LocalDate today);

    /**
     * Compte les emprunts actifs d'un étudiant.
     * Utilisé pour vérifier le quota (max 3 emprunts simultanés).
     *
     * @param etudiantId identifiant de l'étudiant
     * @return nombre d'emprunts actifs
     */
    @Query("""
        SELECT COUNT(e) FROM Emprunt e
        WHERE e.etudiant.id = :etudiantId
        AND e.statut <> 'RENDU'
        """)
    long countEmpruntsActifsByEtudiant(@Param("etudiantId") Long etudiantId);

    /**
     * Compte les emprunts par statut.
     * @param statut statut de l emprunt
     * @return nombre d emprunts ayant ce statut
     */
    long countByStatut(StatutEmprunt statut);

    // ══════════════════════════════════════════════════════
    // NOUVELLES MÉTHODES — BibliothécaireController
    // ══════════════════════════════════════════════════════

    /**
     * Recherche paginée des emprunts par statut.
     *
     * @param statut   statut recherché
     * @param pageable paramètres de pagination
     * @return page d'emprunts
     */
    Page<Emprunt> findByStatut(StatutEmprunt statut, Pageable pageable);

    /**
     * Recherche paginée des emprunts par liste de statuts.
     *
     * @param statuts  liste des statuts recherchés
     * @param pageable paramètres de pagination
     * @return page d'emprunts
     */
    Page<Emprunt> findByStatutIn(List<StatutEmprunt> statuts, Pageable pageable);
}
