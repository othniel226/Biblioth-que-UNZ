package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.Role;
import com.unz.bibliotheque.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour l'inscription publique des étudiants.
 *
 * Routes :
 *   GET  /auth/register → Afficher le formulaire d'inscription
 *   POST /auth/register → Traiter l'inscription
 */
@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UtilisateurRepository utilisateurRepo;
    private final PasswordEncoder       passwordEncoder;

    /**
     * Affiche le formulaire d'inscription (accessible sans connexion).
     */
    @GetMapping("/auth/register")
    public String afficherFormulaire() {
        return "auth/register";
    }

    /**
     * Traite le formulaire d'inscription d'un étudiant.
     * Crée le compte et redirige vers la page de connexion.
     */
    @PostMapping("/auth/register")
    public String inscrire(
        @RequestParam String prenom,
        @RequestParam String nom,
        @RequestParam String email,
        @RequestParam String matricule,
        @RequestParam String filiere,
        @RequestParam String niveau,
        @RequestParam String motDePasse,
        @RequestParam(required = false) String confirmMotDePasse,
        RedirectAttributes ra
    ) {
        // Vérification email unique
        if (utilisateurRepo.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Cet email est déjà utilisé. Veuillez vous connecter.");
            return "redirect:/auth/register";
        }

        // Vérification mot de passe
        if (confirmMotDePasse != null && !motDePasse.equals(confirmMotDePasse)) {
            ra.addFlashAttribute("error", "Les mots de passe ne correspondent pas.");
            return "redirect:/auth/register";
        }

        if (motDePasse.length() < 6) {
            ra.addFlashAttribute("error", "Le mot de passe doit contenir au moins 6 caractères.");
            return "redirect:/auth/register";
        }

        // Créer le compte étudiant
        Etudiant etudiant = new Etudiant();
        etudiant.setPrenom(prenom.trim());
        etudiant.setNom(nom.trim());
        etudiant.setEmail(email.trim().toLowerCase());
        etudiant.setMotDePasse(passwordEncoder.encode(motDePasse));
        etudiant.setRole(Role.ETUDIANT);
        etudiant.setActif(true);
        etudiant.setMatricule(matricule.trim().toUpperCase());
        etudiant.setFiliere(filiere);
        etudiant.setNiveau(niveau);

        utilisateurRepo.save(etudiant);

        ra.addFlashAttribute("success",
            "Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
        return "redirect:/auth/login";
    }
}
