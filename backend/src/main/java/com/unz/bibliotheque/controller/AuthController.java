package com.unz.bibliotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur gérant les pages d'authentification.
 *
 * Spring Security gère automatiquement le traitement du formulaire de connexion
 * via la configuration dans SecurityConfig.
 * Ce contrôleur se contente d'afficher les pages Thymeleaf.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    /**
     * Affiche la page de connexion.
     * Accessible sans authentification (configuré dans SecurityConfig).
     *
     * @return nom du template Thymeleaf : templates/auth/login.html
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * Redirige vers la page de connexion après déconnexion.
     * Spring Security gère automatiquement la déconnexion via /auth/logout.
     *
     * @return redirection vers la page de connexion
     */
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "redirect:/auth/login?logout=true";
    }
}
