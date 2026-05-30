package com.unz.bibliotheque.dto.response;

import com.unz.bibliotheque.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse après authentification réussie.
 * Contient le token JWT et les informations de base de l'utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Long id;
    private String email;
    private String nomComplet;
    private Role role;
}
