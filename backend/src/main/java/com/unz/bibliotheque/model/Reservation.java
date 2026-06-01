package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    // ── EAGER : évite LazyInitializationException dans Thymeleaf ────
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ouvrage_id", nullable = false)
    private Ouvrage ouvrage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime dateReservation = LocalDateTime.now();

    @Column
    private LocalDateTime dateConfirmation;

    @Column
    private LocalDateTime dateExpiration;

    @Column
    private Integer positionFile;

    public void confirmer(int delaiHeures) {
        this.statut = StatutReservation.CONFIRMEE;
        this.dateConfirmation = LocalDateTime.now();
        this.dateExpiration = LocalDateTime.now().plusHours(delaiHeures);
    }

    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }
}
