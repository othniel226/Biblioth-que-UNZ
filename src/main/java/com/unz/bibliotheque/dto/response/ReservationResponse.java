package com.unz.bibliotheque.dto.response;

import com.unz.bibliotheque.model.enums.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour afficher une réservation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;

    // Étudiant
    private Long etudiantId;
    private String etudiantNomComplet;

    // Ouvrage
    private Long ouvrageId;
    private String ouvrageTitre;
    private String ouvrageAuteur;

    // Statut & dates
    private StatutReservation statut;
    private LocalDateTime dateReservation;
    private LocalDateTime dateConfirmation;
    private LocalDateTime dateExpiration;
    private Integer positionFile;
}
