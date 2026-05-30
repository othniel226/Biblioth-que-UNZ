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

/**
 * Repository JPA pour l'entité Ouvrage.
 * Fournit des méthodes de recherche avancée pour le catalogue.
 */
@Repository
public interface OuvrageRepository extends JpaRepository<Ouvrage, Long> {

    /**
     * Recherche paginée et filtrée dans le catalogue.
     * Combine recherche textuelle (titre, auteur, ISBN) et filtres optionnels.
     *
     * @param q          terme de recherche (titre, auteur, ou ISBN) — peut être null
     * @param categorieId identifiant de catégorie pour filtrer — peut être null
     * @param disponible  true pour afficher uniquement les ouvrages disponibles — peut être null
     * @param pageable   paramètres de pagination et tri
     * @return page d'ouvrages correspondant aux critères
     */
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
        Pageable pageable
    );

    /**
     * Recherche un ouvrage par son ISBN.
     *
     * @param isbn ISBN de l'ouvrage
     * @return Optional contenant l'ouvrage, ou vide si introuvable
     */
    Optional<Ouvrage> findByIsbn(String isbn);

    /**
     * Retourne les ouvrages les plus empruntés (pour le dashboard admin).
     *
     * @param pageable pagination (généralement top 10)
     * @return liste des ouvrages triés par nombre d'emprunts décroissant
     */
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
