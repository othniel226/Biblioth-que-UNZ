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

@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {

    Optional<Exemplaire> findByCodeBarres(String codeBarres);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Exemplaire e WHERE e.id = :id")
    Optional<Exemplaire> findByIdWithLock(@Param("id") Long id);

    long countByStatut(StatutExemplaire statut);

    List<Exemplaire> findByStatut(StatutExemplaire statut);

    List<Exemplaire> findByOuvrageId(Long ouvrageId);

    // ── CatalogueController : premier exemplaire disponible ──────────
    Optional<Exemplaire> findFirstByOuvrageIdAndStatut(Long ouvrageId, StatutExemplaire statut);

    // ── BibliothecaireController : tous les exemplaires disponibles ───
    @Query("SELECT e FROM Exemplaire e WHERE e.statut = 'DISPONIBLE' ORDER BY e.ouvrage.titre")
    List<Exemplaire> findByStatutDisponible();
}
