package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.Utilisateur;
import com.unz.bibliotheque.model.enums.Role;
import com.unz.bibliotheque.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controleur de redirection post-connexion.
 * Redirige l utilisateur vers le bon tableau de bord selon son role.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/auth/login";
        }

        String email = auth.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);
        
        if (utilisateur == null) {
            return "redirect:/auth/login";
        }

        return switch (utilisateur.getRole()) {
            case ADMINISTRATEUR -> "redirect:/admin/tableau-de-bord";
            case BIBLIOTHECAIRE -> "redirect:/bibliothecaire/tableau-de-bord";
            case ETUDIANT -> "redirect:/etudiant/tableau-de-bord";
        };
    }
}
