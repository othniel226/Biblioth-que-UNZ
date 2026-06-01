package com.unz.bibliotheque.dto.response;

import com.unz.bibliotheque.model.enums.StatutEmprunt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour afficher un emprunt avec toutes ses informations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpruntResponse {

    private Long id;

    // Étudiant
    private Long etudiantId;
    private String etudiantNomComplet;
    private String etudiantEmail;

    // Exemplaire & Ouvrage
    private Long exemplaireId;
    private String exemplaireCodeBarres;
    private Long ouvrageId;
    private String ouvrageTitre;
    private String ouvrageAuteur;
    private String ouvrageIsbn;

    // Dates
    private LocalDateTime dateEmprunt;
    private LocalDate dateRetourPrevu;
    private LocalDate dateRetourReel;

    // Statut
    private StatutEmprunt statut;
    private Boolean prolonge;
    private Boolean enRetard;
    private Long nombreJoursRetard;

    // Pénalité
    private BigDecimal penaliteMontant;
    private Boolean penalitePayee;

    // Bibliothécaire (si enregistré au comptoir)
    private Long bibliothecaireId;
    private String bibliothecaireNomComplet;
}
