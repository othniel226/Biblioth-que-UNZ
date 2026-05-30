package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.repository.*;
import com.unz.bibliotheque.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur de l'espace étudiant.
 *
 * Accessible aux rôles ETUDIANT, BIBLIOTHECAIRE et ADMINISTRATEUR.
 * Chaque étudiant ne peut accéder qu'à ses propres données.
 *
 * US-05 : Tableau de bord étudiant
 * US-08a : Historique des emprunts
 * US-06 : Demande de prolongation
 * US-07 : Annuler une réservation
 */
@Controller
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class EtudiantController {

    private final EmpruntService         empruntService;
    private final ReservationService     reservationService;
    private final EtudiantRepository     etudiantRepo;
    private final NotificationRepository notifRepo;
    private final PenaliteRepository     penaliteRepo;
    private final UtilisateurRepository  utilisateurRepo;

    /**
     * Affiche le tableau de bord de l'étudiant connecté.
     * Charge les emprunts en cours, réservations, notifications et pénalités.
     *
     * @param user  utilisateur Spring Security authentifié
     * @param model modèle Thymeleaf
     * @return template etudiant/tableau-de-bord
     */
    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(
        @AuthenticationPrincipal UserDetails user,
        Model model
    ) {
        // Trouver l'utilisateur connecté puis l'étudiant correspondant
        var utilisateur = utilisateurRepo.findByEmail(user.getUsername()).orElseThrow();
        var etudiant    = etudiantRepo.findById(utilisateur.getId()).orElseThrow();
        Long id         = etudiant.getId();

        // Charger les données du tableau de bord
        var empruntsEnCours   = empruntService.getEmpruntsEnCours(id);
        var reservations      = reservationService.getReservationsActives(id);
        var notifications     = notifRepo.findTopByDestinataireId(id, PageRequest.of(0, 5));
        var penalitesImpayees = penaliteRepo.sumMontantImpayeByEtudiantId(id);
        var empruntsEnRetard  = empruntsEnCours.stream().filter(Emprunt::estEnRetard).toList();
        var totalEmprunts     = empruntService.getHistoriqueEtudiant(id, Pageable.unpaged()).getTotalElements();

        // Passer toutes les données au template Thymeleaf
        model.addAttribute("etudiant",          etudiant);
        model.addAttribute("empruntsEnCours",   empruntsEnCours);
        model.addAttribute("empruntsEnRetard",  empruntsEnRetard);
        model.addAttribute("reservations",      reservations);
        model.addAttribute("notifications",     notifications);
        model.addAttribute("penalitesImpayees", penalitesImpayees);
        model.addAttribute("nbEmpruntsEnCours", empruntsEnCours.size());
        model.addAttribute("nbRetards",         empruntsEnRetard.size());
        model.addAttribute("nbReservations",    reservations.size());
        model.addAttribute("nbNotifs",          notifRepo.countByDestinataireIdAndLuFalse(id));
        model.addAttribute("totalEmprunts",     totalEmprunts);

        return "etudiant/tableau-de-bord";
    }

    /**
     * Affiche l'historique paginé des emprunts de l'étudiant.
     *
     * @param user  utilisateur authentifié
     * @param page  numéro de page (0-indexé)
     * @param model modèle Thymeleaf
     * @return template etudiant/historique
     */
    @GetMapping("/historique")
    public String historique(
        @AuthenticationPrincipal UserDetails user,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var utilisateur = utilisateurRepo.findByEmail(user.getUsername()).orElseThrow();
        var etudiant    = etudiantRepo.findById(utilisateur.getId()).orElseThrow();

        var historique = empruntService.getHistoriqueEtudiant(
            etudiant.getId(),
            PageRequest.of(page, 10, Sort.by("dateEmprunt").descending())
        );

        model.addAttribute("etudiant",  etudiant);
        model.addAttribute("historique", historique);

        return "etudiant/historique";
    }

    /**
     * Traite une demande de prolongation d'emprunt.
     * Redirige vers le tableau de bord avec un message de succès ou d'erreur.
     *
     * @param id   identifiant de l'emprunt à prolonger
     * @param user utilisateur authentifié
     * @param ra   attributs flash (messages entre redirections)
     * @return redirection vers le tableau de bord
     */
    @PostMapping("/emprunts/{id}/prolonger")
    public String prolonger(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails user,
        RedirectAttributes ra
    ) {
        var utilisateur = utilisateurRepo.findByEmail(user.getUsername()).orElseThrow();
        var etudiant    = etudiantRepo.findById(utilisateur.getId()).orElseThrow();

        try {
            empruntService.prolongerEmprunt(id, etudiant.getId());
            ra.addFlashAttribute("success", "Prolongation accordée avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/etudiant/tableau-de-bord";
    }

    /**
     * Traite l'annulation d'une réservation.
     *
     * @param id   identifiant de la réservation à annuler
     * @param user utilisateur authentifié
     * @param ra   attributs flash
     * @return redirection vers le tableau de bord
     */
    @PostMapping("/reservations/{id}/annuler")
    public String annulerReservation(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails user,
        RedirectAttributes ra
    ) {
        var utilisateur = utilisateurRepo.findByEmail(user.getUsername()).orElseThrow();
        var etudiant    = etudiantRepo.findById(utilisateur.getId()).orElseThrow();

        try {
            reservationService.annulerReservation(id, etudiant.getId());
            ra.addFlashAttribute("success", "Réservation annulée avec succès.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/etudiant/tableau-de-bord";
    }
}
