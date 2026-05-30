package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Reservation;
import com.unz.bibliotheque.model.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour l entite Reservation.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Recherche les reservations d un etudiant par statuts.
     *
     * @param etudiantId identifiant de l etudiant
     * @param statuts    liste des statuts recherches
     * @return liste des reservations correspondantes
     */
    List<Reservation> findByEtudiantIdAndStatutIn(Long etudiantId, List<StatutReservation> statuts);

    /**
     * Compte les reservations actives d un etudiant pour un ouvrage donne.
     * Utilise pour eviter les doublons de reservation.
     *
     * @param etudiantId identifiant de l etudiant
     * @param ouvrageId  identifiant de l ouvrage
     * @return nombre de reservations actives
     */
    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.etudiant.id = :etudiantId
        AND r.ouvrage.id = :ouvrageId
        AND r.statut IN ('EN_ATTENTE', 'CONFIRMEE')
        """)
    long countActiveByEtudiantAndOuvrage(
        @Param("etudiantId") Long etudiantId,
        @Param("ouvrageId") Long ouvrageId
    );

    /**
     * Retourne la prochaine reservation en attente pour un ouvrage (file FIFO).
     * Triee par dateReservation croissante (premier arrive, premier servi).
     *
     * @param ouvrageId identifiant de l ouvrage
     * @return Optional contenant la prochaine reservation, ou vide si file vide
     */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.ouvrage.id = :ouvrageId
        AND r.statut = 'EN_ATTENTE'
        ORDER BY r.dateReservation ASC
        """)
    Optional<Reservation> findProchainEnAttenteByOuvrageId(@Param("ouvrageId") Long ouvrageId);

    /**
     * Compte les reservations par statut.
     * @param statut statut de la reservation
     * @return nombre de reservations ayant ce statut
     */
    long countByStatut(StatutReservation statut);
}
