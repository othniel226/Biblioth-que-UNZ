package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.ConfigurationSysteme;
import com.unz.bibliotheque.model.Utilisateur;
import com.unz.bibliotheque.model.enums.Role;
import com.unz.bibliotheque.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UtilisateurRepository utilisateurRepo;
    private final EmpruntRepository empruntRepo;
    private final OuvrageRepository ouvrageRepo;
    private final ReservationRepository reservationRepo;
    private final ExemplaireRepository exemplaireRepo;
    private final PenaliteRepository penaliteRepo;
    private final NotificationRepository notificationRepo;
    private final ConfigurationRepository configurationRepo;

    /* ========== TABLEAU DE BORD ========== */
    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(Model model) {
        model.addAttribute("nbUtilisateurs", utilisateurRepo.count());
        model.addAttribute("nbEtudiants", utilisateurRepo.countByRole(Role.ETUDIANT));
        model.addAttribute("nbBibliothecaires", utilisateurRepo.countByRole(Role.BIBLIOTHECAIRE));
        model.addAttribute("nbEmprunts", empruntRepo.count());
        model.addAttribute("nbOuvrages", ouvrageRepo.count());
        model.addAttribute("nbExemplaires", exemplaireRepo.count());
        model.addAttribute("nbReservations", reservationRepo.count());
        model.addAttribute("nbPenalitesImpayees", penaliteRepo.countByPayeeFalse());
        model.addAttribute("nbNotificationsNonLues", notificationRepo.countByLuFalse());
        
        return "admin/tableau-de-bord";
    }

    /* ========== UTILISATEURS ========== */
    @GetMapping("/utilisateurs")
    public String listeUtilisateurs(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Role role,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var utilisateurs = utilisateurRepo.findAll(PageRequest.of(page, 20));
        
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("roles", Role.values());
        model.addAttribute("q", q);
        model.addAttribute("role", role);
        model.addAttribute("pageActuelle", page);
        
        return "admin/utilisateurs";
    }

    @GetMapping("/utilisateurs/{id}")
    public String detailUtilisateur(@PathVariable Long id, Model model) {
        var utilisateur = utilisateurRepo.findById(id)
            .orElseThrow(() -> new com.unz.bibliotheque.exception.ResourceNotFoundException(
                "Utilisateur introuvable (id=" + id + ")"
            ));
        model.addAttribute("utilisateur", utilisateur);
        return "admin/utilisateur-detail";
    }

    @PostMapping("/utilisateurs/{id}/activer")
    public String activerUtilisateur(@PathVariable Long id, RedirectAttributes ra) {
        var user = utilisateurRepo.findById(id).orElseThrow();
        user.setActif(true);
        utilisateurRepo.save(user);
        ra.addFlashAttribute("success", "Compte active avec succes.");
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/desactiver")
    public String desactiverUtilisateur(@PathVariable Long id, RedirectAttributes ra) {
        var user = utilisateurRepo.findById(id).orElseThrow();
        // Protection : empecher la desactivation de son propre compte
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (user.getEmail().equals(auth.getName())) {
            ra.addFlashAttribute("error", "Vous ne pouvez pas desactiver votre propre compte !");
            return "redirect:/admin/utilisateurs";
        }
        user.setActif(false);
        utilisateurRepo.save(user);
        ra.addFlashAttribute("success", "Compte desactive avec succes.");
        return "redirect:/admin/utilisateurs";
    }

    /* ========== RAPPORTS ========== */
    @GetMapping("/rapports")
    public String rapports(Model model) {
        model.addAttribute("nbEmpruntsTotal", empruntRepo.count());
        model.addAttribute("nbEmpruntsEnCours", empruntRepo.countByStatut(com.unz.bibliotheque.model.enums.StatutEmprunt.EN_COURS));
        model.addAttribute("nbEmpruntsEnRetard", empruntRepo.countByStatut(com.unz.bibliotheque.model.enums.StatutEmprunt.EN_RETARD));
        model.addAttribute("nbOuvrages", ouvrageRepo.count());
        model.addAttribute("nbEtudiants", utilisateurRepo.countByRole(Role.ETUDIANT));
        model.addAttribute("topOuvrages", ouvrageRepo.findTopEmpruntes(PageRequest.of(0, 10)));
        
        return "admin/rapports";
    }

    /* ========== CONFIGURATION ========== */
    @GetMapping("/configuration")
    public String configuration(Model model) {
        var configs = configurationRepo.findAll();
        model.addAttribute("configurations", configs);
        return "admin/configuration";
    }

    @PostMapping("/configuration")
    public String sauvegarderConfiguration(
        @RequestParam String cle,
        @RequestParam String valeur,
        RedirectAttributes ra
    ) {
        var config = configurationRepo.findByCle(cle).orElse(new ConfigurationSysteme());
        config.setCle(cle);
        config.setValeur(valeur);
        configurationRepo.save(config);
        ra.addFlashAttribute("success", "Configuration mise a jour.");
        return "redirect:/admin/configuration";
    }
}
