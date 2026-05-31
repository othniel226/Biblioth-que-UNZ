package com.unz.bibliotheque.dto.request;

import com.unz.bibliotheque.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour créer ou modifier un utilisateur.
 * Gère les champs communs + spécifiques selon le rôle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

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
