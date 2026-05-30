package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutExemplaire;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entité représentant un exemplaire physique d'un ouvrage.
 * Chaque exemplaire a un code-barres unique et un statut propre.
 *
 * Un ouvrage peut avoir plusieurs exemplaires, mais chaque exemplaire
 * appartient à un seul ouvrage (relation Many-to-One).
 */
@Entity
@Table(name = "exemplaires")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code-barres unique de l'exemplaire (ex: EX-001-A) */
    @NotBlank(message = "Le code-barres est obligatoire")
    @Column(nullable = false, unique = true, length = 50)
    private String codeBarres;

    /**
     * Statut actuel de l'exemplaire.
     * Géré via le Pattern State implicitement (transitions de statut).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutExemplaire statut = StatutExemplaire.DISPONIBLE;

    /** Ouvrage auquel appartient cet exemplaire */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ouvrage_id", nullable = false)
    private Ouvrage ouvrage;

    /** Notes sur l'état physique de l'exemplaire */
    @Column(length = 300)
    private String notes;

    /**
     * Marque l'exemplaire comme emprunté.
     * Appelé lors de la création d'un emprunt.
     */
    public void marquerEmprunte() {
        this.statut = StatutExemplaire.EMPRUNTE;
    }

    /**
     * Marque l'exemplaire comme disponible.
     * Appelé lors de l'enregistrement d'un retour.
     */
    public void marquerDisponible() {
        this.statut = StatutExemplaire.DISPONIBLE;
    }

    /**
     * Marque l'exemplaire comme réservé (pour une réservation confirmée).
     */
    public void marquerReserve() {
        this.statut = StatutExemplaire.RESERVE;
    }
}
