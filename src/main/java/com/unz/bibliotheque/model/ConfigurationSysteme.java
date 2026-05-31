package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entité de configuration du système bibliothèque.
 * Permet à l'administrateur de modifier les paramètres métier
 * sans redémarrer l'application.
 *
 * Clés de configuration disponibles :
 *   - DUREE_EMPRUNT_JOURS         : durée par défaut (14 jours)
 *   - MAX_EMPRUNTS_SIMULTANES     : quota par étudiant (3)
 *   - MAX_RESERVATIONS_SIMULTANES : quota réservations (5)
 *   - PENALITE_PAR_JOUR_FCFA      : montant journalier (100 FCFA)
 *   - SEUIL_BLOCAGE_FCFA          : seuil de blocage compte (500 FCFA)
 *   - DUREE_RESERVATION_HEURES    : délai confirmation réservation (48h)
 */
@Entity
@Table(name = "configurations_systeme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConfigurationSysteme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Clé de configuration (identifiant unique) */
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String cle;

    /** Valeur de la configuration (stockée en String, convertie à l'usage) */
    @NotBlank
    @Column(nullable = false, length = 500)
    private String valeur;

    /** Description lisible par l'administrateur */
    @Column(length = 300)
    private String description;

    /**
     * Retourne la valeur convertie en entier.
     *
     * @return valeur entière de la configuration
     * @throws NumberFormatException si la valeur n'est pas un entier valide
     */
    public int getValeurAsInt() {
        return Integer.parseInt(valeur.trim());
    }

    /**
     * Retourne la valeur convertie en décimal (pour les montants FCFA).
     *
     * @return valeur décimale de la configuration
     */
    public java.math.BigDecimal getValeurAsDecimal() {
        return new java.math.BigDecimal(valeur.trim());
    }
}
