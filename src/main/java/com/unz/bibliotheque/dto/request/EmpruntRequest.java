package com.unz.bibliotheque.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour créer un emprunt.
 * Utilisé par le bibliothécaire ou l'étudiant (self-service).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpruntRequest {

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "L'identifiant de l'exemplaire est obligatoire")
    private Long exemplaireId;
}
