package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entité représentant un administrateur du système Bibliothèque UNZ.
 * Hérite de {@link Utilisateur} avec la stratégie JOINED.
 *
 * L'administrateur dispose de TOUS les droits :
 *   - gestion des comptes utilisateurs (création, activation, désactivation)
 *   - configuration du système (durée emprunt, pénalités, quotas)
 *   - génération de rapports et statistiques
 *   - consultation du journal d'activités
 *   - + tous les droits du bibliothécaire
 */
@Entity
@Table(name = "administrateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends Utilisateur {

    /** Département de rattachement administratif */
    @Column(length = 100)
    private String departement;
}
