package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Penalite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Penalite p WHERE p.emprunt.etudiant.id = :etudiantId AND p.payee = false")
    BigDecimal sumMontantImpayeByEtudiantId(@Param("etudiantId") Long etudiantId);

    long countByPayeeFalse();

    Page<Penalite> findByPayee(Boolean payee, Pageable pageable);

    // ── BibliothecaireController ─────────────────────────────────────
    Page<Penalite> findByPayeeFalseOrderByDateCreationDesc(Pageable pageable);
}
