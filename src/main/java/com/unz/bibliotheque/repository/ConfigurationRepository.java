package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.ConfigurationSysteme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository JPA pour l'entité ConfigurationSysteme.
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationSysteme, Long> {

    /**
     * Recherche une configuration par sa clé.
     *
     * @param cle clé de configuration (ex: DUREE_EMPRUNT_JOURS)
     * @return Optional contenant la configuration, ou vide si inexistante
     */
    Optional<ConfigurationSysteme> findByCle(String cle);
}
