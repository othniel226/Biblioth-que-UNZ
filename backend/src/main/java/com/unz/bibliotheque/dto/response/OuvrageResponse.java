package com.unz.bibliotheque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse pour afficher un ouvrage du catalogue.
 * Inclut les infos de disponibilité pour l'UI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OuvrageResponse {

    private Long id;
    private String titre;
    private String isbn;
    private String auteur;
    private String editeur;
    private Integer anneePublication;
    private String description;
    private String imageCouverture;

    // Catégorie
    private Long categorieId;
    private String categorieNom;

    // Disponibilité
    private Boolean disponible;
    private Long nombreExemplairesDisponibles;
    private Long nombreExemplairesTotal;

    private Boolean archive;
}
