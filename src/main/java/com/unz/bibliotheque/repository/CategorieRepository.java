package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository JPA pour l'entité Categorie.
 */
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    /**
     * Recherche une catégorie par son nom.
     *
     * @param nom nom de la catégorie
     * @return Optional contenant la catégorie, ou vide si introuvable
     */
    Optional<Categorie> findByNom(String nom);
}
