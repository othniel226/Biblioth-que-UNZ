package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.enums.StatutExemplaire;
import com.unz.bibliotheque.repository.*;
import com.unz.bibliotheque.service.EmpruntService;
import com.unz.bibliotheque.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final OuvrageRepository ouvrageRepo;
    private final CategorieRepository categorieRepo;
    private final ExemplaireRepository exemplaireRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final EmpruntService empruntService;
    private final ReservationService reservationService;

    /* ========== LISTE DU CATALOGUE ========== */
    @GetMapping("")
    public String catalogue(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Long categorieId,
        @RequestParam(required = false) Boolean disponible,
        @RequestParam(defaultValue = "0") int page,
        Model model,
        Authentication auth
    ) {
        var ouvrages = ouvrageRepo.findByCriteres(q, categorieId, disponible,
            PageRequest.of(page, 12));
        var categories = categorieRepo.findAll();

        model.addAttribute("ouvrages", ouvrages);
        model.addAttribute("categories", categories);
        model.addAttribute("q", q);
        model.addAttribute("categorieId", categorieId);
        model.addAttribute("disponible", disponible);
        model.addAttribute("pageActuelle", page);

        // Ajouter l'étudiant si connecté
        if (auth != null && auth.isAuthenticated()) {
            utilisateurRepo.findByEmail(auth.getName())
                .ifPresent(u -> model.addAttribute("utilisateur", u));
        }

        return "catalogue/liste";
    }

    /* ========== DÉTAIL D'UN OUVRAGE ========== */
    @GetMapping("/{id}")
    public String detailOuvrage(
        @PathVariable Long id,
        Model model,
        Authentication auth
    ) {
        var ouvrage = ouvrageRepo.findById(id)
            .orElseThrow(() -> new com.unz.bibliotheque.exception.ResourceNotFoundException(
                "Ouvrage introuvable"));

        model.addAttribute("ouvrage", ouvrage);
        model.addAttribute("nbDisponibles", ouvrage.getNombreExemplairesDisponibles());

        if (auth != null && auth.isAuthenticated()) {
            utilisateurRepo.findByEmail(auth.getName())
                .ifPresent(u -> model.addAttribute("utilisateur", u));
        }

        return "catalogue/detail";
    }

    /* ========== EMPRUNTER UN OUVRAGE ========== */
    @PostMapping("/{id}/emprunter")
    public String emprunterOuvrage(
        @PathVariable Long id,
        Authentication auth,
        RedirectAttributes ra
    ) {
        try {
            Etudiant etudiant = (Etudiant) utilisateurRepo.findByEmail(auth.getName())
                .orElseThrow();

            // Trouver un exemplaire disponible
            var exemplaireOpt = exemplaireRepo.findFirstByOuvrageIdAndStatut(
                id, StatutExemplaire.DISPONIBLE);

            if (exemplaireOpt.isEmpty()) {
                ra.addFlashAttribute("error",
                    "Aucun exemplaire disponible. Vous pouvez réserver cet ouvrage.");
                return "redirect:/catalogue/" + id;
            }

            empruntService.creerEmprunt(etudiant.getId(), exemplaireOpt.get().getId());
            ra.addFlashAttribute("success",
                "Emprunt effectué avec succès ! Durée : 14 jours.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/etudiant/tableau-de-bord";
    }

    /* ========== RÉSERVER UN OUVRAGE ========== */
    @PostMapping("/{id}/reserver")
    public String reserverOuvrage(
        @PathVariable Long id,
        Authentication auth,
        RedirectAttributes ra
    ) {
        try {
            Etudiant etudiant = (Etudiant) utilisateurRepo.findByEmail(auth.getName())
                .orElseThrow();
            reservationService.creerReservation(etudiant.getId(), id);
            ra.addFlashAttribute("success",
                "Réservation effectuée ! Vous serez notifié par email dès qu'un exemplaire sera disponible.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/catalogue/" + id;
    }
}
