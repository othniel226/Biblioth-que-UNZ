package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Reservation;
import com.unz.bibliotheque.model.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByEtudiantIdAndStatutIn(Long etudiantId, List<StatutReservation> statuts);

    long countByEtudiantIdAndStatutIn(Long etudiantId, List<StatutReservation> statuts);

    @Query("SELECT r FROM Reservation r WHERE r.ouvrage.id = :ouvrageId AND r.statut = 'EN_ATTENTE' ORDER BY r.dateReservation ASC")
    Optional<Reservation> findProchainEnAttenteByOuvrageId(@Param("ouvrageId") Long ouvrageId);

    boolean existsByEtudiantIdAndOuvrageIdAndStatutIn(
        Long etudiantId, Long ouvrageId, List<StatutReservation> statuts);

    // ── BibliothecaireRestController ─────────────────────────────────
    long countByStatut(StatutReservation statut);

    // ── ReservationService ───────────────────────────────────────────
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.etudiant.id = :etudiantId AND r.ouvrage.id = :ouvrageId AND r.statut IN ('EN_ATTENTE','CONFIRMEE')")
    long countActiveByEtudiantAndOuvrage(
        @Param("etudiantId") Long etudiantId,
        @Param("ouvrageId") Long ouvrageId);
}
