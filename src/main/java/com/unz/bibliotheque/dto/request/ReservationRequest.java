package com.unz.bibliotheque.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour créer une réservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "L'identifiant de l'ouvrage est obligatoire")
    private Long ouvrageId;
}
