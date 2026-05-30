package com.unz.bibliotheque.dto.response;

import com.unz.bibliotheque.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour afficher un utilisateur (sans mot de passe).
 * Inclut les champs spécifiques selon le sous-type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {

    private Long id;
    private String prenom;
    private String nom;
    private String nomComplet;
    private String email;
    private Role role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // === Champs spécifiques Étudiant ===
    private String matricule;
    private String filiere;
    private String niveau;

    // === Champs spécifiques Bibliothécaire ===
    private String badgeNumero;
    private String service;

    // === Champs spécifiques Administrateur ===
    private String departement;
}
