package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entité représentant une réservation d'ouvrage.
 *
 * Quand un ouvrage est indisponible, les étudiants peuvent le réserver.
 * Les réservations sont gérées en file FIFO (Premier arrivé, premier servi).
 *
 * Quand un exemplaire est rendu, le système notifie automatiquement le prochain
 * étudiant dans la file (Pattern Observer via ExemplaireRetourneEvent).
 */
@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Étudiant ayant effectué la réservation */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    /** Ouvrage réservé (pas l'exemplaire, car il n'est pas encore connu) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ouvrage_id", nullable = false)
    private Ouvrage ouvrage;

    /** Statut actuel de la réservation */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    /** Date et heure de création de la réservation (utilisée pour le FIFO) */
    @Column(nullable = false)
    private LocalDateTime dateReservation = LocalDateTime.now();

    /** Date de confirmation (quand l'exemplaire est disponible) */
    @Column
    private LocalDateTime dateConfirmation;

    /**
     * Date d'expiration de la réservation confirmée.
     * L'étudiant a 48h pour venir chercher l'ouvrage après confirmation.
     */
    @Column
    private LocalDateTime dateExpiration;

    /** Position dans la file d'attente FIFO (1 = premier) */
    @Column
    private Integer positionFile;

    /**
     * Confirme la réservation et définit la date d'expiration.
     *
     * @param delaiHeures délai en heures avant expiration (généralement 48h)
     */
    public void confirmer(int delaiHeures) {
        this.statut = StatutReservation.CONFIRMEE;
        this.dateConfirmation = LocalDateTime.now();
        this.dateExpiration = LocalDateTime.now().plusHours(delaiHeures);
    }

    /**
     * Annule la réservation.
     */
    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }
}
