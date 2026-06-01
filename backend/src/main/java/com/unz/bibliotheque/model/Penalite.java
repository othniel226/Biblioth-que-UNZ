package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une pénalité de retard.
 *
 * Cardinalité corrigée : un emprunt génère au MAXIMUM une pénalité (0..1).
 * Le montant est calculé par le Pattern Strategy (PenaliteStrategy).
 *
 * Deux stratégies disponibles :
 *   - TarifFixeStrategy   : montant fixe par jour (ex: 100 FCFA/jour)
 *   - TarifProgressifStrategy : tarif qui augmente avec le nombre de jours
 */
@Entity
@Table(name = "penalites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Penalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Emprunt concerné par cette pénalité.
     * Relation One-to-One (un emprunt → une pénalité maximum).
     */
    @OneToOne
    @JoinColumn(name = "emprunt_id", nullable = false, unique = true)
    private Emprunt emprunt;

    /** Montant de la pénalité en FCFA */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant = BigDecimal.ZERO;

    /** Nombre de jours de retard ayant généré cette pénalité */
    @Column(nullable = false)
    private Long joursRetard = 0L;

    /** Indique si la pénalité a été encaissée par le bibliothécaire */
    @Column(nullable = false)
    private Boolean payee = false;

    /** Date et heure de création de la pénalité */
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    /** Date et heure d'encaissement (null si non payée) */
    @Column
    private LocalDateTime datePaiement;

    /** Nom de la stratégie utilisée pour le calcul (pour traçabilité) */
    @Column(length = 50)
    private String strategieUtilisee;

    /**
     * Marque la pénalité comme payée et enregistre la date de paiement.
     */
    public void marquerPayee() {
        this.payee = true;
        this.datePaiement = LocalDateTime.now();
    }
}
