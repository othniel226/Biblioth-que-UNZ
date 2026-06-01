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
import java.util.Optional;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {

    @Query("SELECT e FROM Emprunt e WHERE e.etudiant.id = :etudiantId AND e.statut <> 'RENDU' ORDER BY e.dateEmprunt DESC")
    List<Emprunt> findEmpruntsActifsByEtudiant(@Param("etudiantId") Long etudiantId);

    @Query("SELECT e FROM Emprunt e WHERE e.etudiant.id = :etudiantId ORDER BY e.dateEmprunt DESC")
    Page<Emprunt> findHistoriqueByEtudiant(@Param("etudiantId") Long etudiantId, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Emprunt e WHERE e.etudiant.id = :etudiantId AND e.statut <> 'RENDU'")
    long countEmpruntsActifsByEtudiant(@Param("etudiantId") Long etudiantId);

    @Query("SELECT COUNT(e) FROM Emprunt e WHERE e.etudiant.id = :id")
    long countByEtudiantId(@Param("id") Long etudiantId);

    long countByStatut(StatutEmprunt statut);

    // ── RappelRetourScheduler ────────────────────────────────────────
    @Query("SELECT e FROM Emprunt e WHERE e.dateRetourPrevu = :date AND e.statut IN ('EN_COURS','PROLONGE')")
    List<Emprunt> findByDateRetourPrevuAndStatutIn(@Param("date") LocalDate date);

    @Query("SELECT e FROM Emprunt e WHERE e.dateRetourPrevu < :today AND e.statut IN ('EN_COURS','PROLONGE')")
    List<Emprunt> findEmpruntsEnRetard(@Param("today") LocalDate today);

    // ── BibliothecaireController ─────────────────────────────────────
    @Query("SELECT e FROM Emprunt e WHERE e.exemplaire.id = :id AND e.statut IN ('EN_COURS','EN_RETARD','PROLONGE')")
    Optional<Emprunt> findEmpruntActifByExemplaireId(@Param("id") Long exemplaireId);

    @Query("SELECT e FROM Emprunt e WHERE e.statut = :statut ORDER BY e.dateRetourReel DESC")
    Page<Emprunt> findByStatutOrderByDateRetourReelDesc(@Param("statut") StatutEmprunt statut, Pageable pageable);

    Page<Emprunt> findByStatut(StatutEmprunt statut, Pageable pageable);

    Page<Emprunt> findByStatutIn(List<StatutEmprunt> statuts, Pageable pageable);
}
