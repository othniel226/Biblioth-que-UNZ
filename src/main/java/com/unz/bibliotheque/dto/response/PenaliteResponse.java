package com.unz.bibliotheque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour afficher une pénalité.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaliteResponse {

    private Long id;

    // Emprunt associé
    private Long empruntId;
    private String ouvrageTitre;
    private String etudiantNomComplet;

    // Montant & calcul
    private BigDecimal montant;
    private Long joursRetard;
    private String strategieUtilisee;

    // Paiement
    private Boolean payee;
    private LocalDateTime dateCreation;
    private LocalDateTime datePaiement;
}
