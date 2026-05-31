package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entité représentant un bibliothécaire de la Bibliothèque UNZ.
 * Hérite de {@link Utilisateur} avec la stratégie JOINED.
 *
 * Un bibliothécaire peut :
 *   - gérer le catalogue (ajouter, modifier, archiver des ouvrages)
 *   - enregistrer des emprunts au comptoir
 *   - enregistrer des retours d'ouvrages
 *   - encaisser des pénalités de retard
 */
@Entity
@Table(name = "bibliothecaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bibliothecaire extends Utilisateur {

    /** Numéro de badge unique du bibliothécaire */
    @NotBlank(message = "Le numéro de badge est obligatoire")
    @Column(nullable = false, unique = true, length = 20)
    private String badgeNumero;

    /** Service ou département d'affectation (ex: Section Périodiques) */
    @Column(length = 100)
    private String service;
}
