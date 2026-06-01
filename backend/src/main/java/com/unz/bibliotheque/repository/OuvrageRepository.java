package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Ouvrage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {

    Optional<Ouvrage> findByIsbn(String isbn);

    // ── Recherche catalogue (BibliothecaireController) ───────────────
    Page<Ouvrage> findByTitreContainingIgnoreCaseOrAuteurContainingIgnoreCase(
        String titre, String auteur, Pageable pageable);

    // ── Recherche avancée (CatalogueController) ──────────────────────
    @Query("""
        SELECT o FROM Ouvrage o
        WHERE o.archive = false
        AND (:q IS NULL OR LOWER(o.titre) LIKE LOWER(CONCAT('%',:q,'%'))
                        OR LOWER(o.auteur) LIKE LOWER(CONCAT('%',:q,'%'))
                        OR LOWER(o.isbn) LIKE LOWER(CONCAT('%',:q,'%')))
        AND (:categorieId IS NULL OR o.categorie.id = :categorieId)
        AND (:disponible IS NULL OR EXISTS (
            SELECT ex FROM Exemplaire ex
            WHERE ex.ouvrage = o AND ex.statut = 'DISPONIBLE'
        ))
        """)
    Page<Ouvrage> findByCriteres(
        @Param("q") String q,
        @Param("categorieId") Long categorieId,
        @Param("disponible") Boolean disponible,
        Pageable pageable);

    // ── Ancien nom (pour compatibilité) ─────────────────────────────
    @Query("""
        SELECT o FROM Ouvrage o
        WHERE o.archive = false
        AND (:q IS NULL OR LOWER(o.titre) LIKE LOWER(CONCAT('%', :q, '%'))
                        OR LOWER(o.auteur) LIKE LOWER(CONCAT('%', :q, '%'))
                        OR LOWER(o.isbn) LIKE LOWER(CONCAT('%', :q, '%')))
        AND (:categorieId IS NULL OR o.categorie.id = :categorieId)
        AND (:disponible IS NULL OR EXISTS (
            SELECT ex FROM Exemplaire ex
            WHERE ex.ouvrage = o AND ex.statut = 'DISPONIBLE'
        ))
        """)
    Page<Ouvrage> rechercher(
        @Param("q") String q,
        @Param("categorieId") Long categorieId,
        @Param("disponible") Boolean disponible,
        Pageable pageable);

    // ── Top ouvrages empruntés (AdminController) ─────────────────────
    @Query("""
        SELECT o FROM Ouvrage o
        JOIN o.exemplaires ex
        JOIN Emprunt emp ON emp.exemplaire = ex
        WHERE o.archive = false
        GROUP BY o
        ORDER BY COUNT(emp) DESC
        """)
    List<Ouvrage> findTopEmpruntes(Pageable pageable);
}
