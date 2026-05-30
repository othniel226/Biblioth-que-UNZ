package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.repository.*;
import com.unz.bibliotheque.service.EmpruntService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur de l'espace bibliothécaire (pages Thymeleaf).
 *
 * Accessible aux rôles BIBLIOTHECAIRE et ADMINISTRATEUR.
 *
 * US-09 : Gestion des emprunts au comptoir
 * US-10 : Enregistrement des retours
 * US-11 : Gestion des pénalités
 */
@Controller
@RequestMapping("/bibliothecaire")
@RequiredArgsConstructor
public class BibliothecaireController {

    private final EmpruntRepository empruntRepo;
    private final OuvrageRepository ouvrageRepo;
    private final EtudiantRepository etudiantRepo;
    private final ReservationRepository reservationRepo;
    private final ExemplaireRepository exemplaireRepo;
    private final PenaliteRepository penaliteRepo;
    private final UtilisateurRepository utilisateurRepo;

    private final EmpruntService empruntService;

    /* ========== TABLEAU DE BORD ========== */
    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(Model model) {
        model.addAttribute("nbEmpruntsEnCours", empruntRepo.countByStatut(StatutEmprunt.EN_COURS));
        model.addAttribute("nbEmpruntsEnRetard", empruntRepo.countByStatut(StatutEmprunt.EN_RETARD));
        model.addAttribute("nbOuvrages", ouvrageRepo.count());
        model.addAttribute("nbExemplairesDisponibles", exemplaireRepo.countByStatut(com.unz.bibliotheque.model.enums.StatutExemplaire.DISPONIBLE));
        model.addAttribute("nbEtudiants", etudiantRepo.count());
        model.addAttribute("nbReservationsEnAttente", reservationRepo.countByStatut(com.unz.bibliotheque.model.enums.StatutReservation.EN_ATTENTE));
        model.addAttribute("nbPenalitesImpayees", penaliteRepo.countByPayeeFalse());

        return "bibliothecaire/tableau-de-bord";
    }

    /* ========== EMPRUNTS ========== */
    @GetMapping("/emprunts")
    public String listeEmprunts(
        @RequestParam(required = false) StatutEmprunt statut,
        @RequestParam(required = false) String q,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var emprunts = statut != null
            ? empruntRepo.findByStatut(statut, PageRequest.of(page, 20, Sort.by("dateEmprunt").descending()))
            : empruntRepo.findAll(PageRequest.of(page, 20, Sort.by("dateEmprunt").descending()));

        model.addAttribute("emprunts", emprunts);
        model.addAttribute("statuts", StatutEmprunt.values());
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("q", q);
        model.addAttribute("pageActuelle", page);

        return "bibliothecaire/emprunts";
    }

    @GetMapping("/emprunts/nouveau")
    public String nouveauEmprunt(Model model) {
        model.addAttribute("etudiants", etudiantRepo.findAll());
        model.addAttribute("exemplairesDisponibles", exemplaireRepo.findByStatut(com.unz.bibliotheque.model.enums.StatutExemplaire.DISPONIBLE));
        return "bibliothecaire/emprunt-form";
    }

    @PostMapping("/emprunts")
    public String creerEmprunt(
        @RequestParam Long etudiantId,
        @RequestParam Long exemplaireId,
        RedirectAttributes ra
    ) {
        try {
            empruntService.creerEmprunt(etudiantId, exemplaireId);
            ra.addFlashAttribute("success", "Emprunt enregistré avec succès !");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bibliothecaire/emprunts";
    }

    /* ========== RETOURS ========== */
    @GetMapping("/retours")
    public String listeRetours(
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var empruntsEnCours = empruntRepo.findByStatutIn(
            java.util.List.of(StatutEmprunt.EN_COURS, StatutEmprunt.EN_RETARD, StatutEmprunt.PROLONGE),
            PageRequest.of(page, 20, Sort.by("dateRetourPrevu"))
        );

        model.addAttribute("emprunts", empruntsEnCours);
        model.addAttribute("pageActuelle", page);

        return "bibliothecaire/retours";
    }

    @PostMapping("/retours/{empruntId}")
    public String enregistrerRetour(
        @PathVariable Long empruntId,
        RedirectAttributes ra
    ) {
        try {
            Emprunt emprunt = empruntService.enregistrerRetour(empruntId);
            String msg = "Retour enregistré";
            if (emprunt.getPenalite() != null) {
                msg += " — Pénalité : " + emprunt.getPenalite().getMontant() + " FCFA";
            }
            ra.addFlashAttribute("success", msg);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bibliothecaire/retours";
    }

    /* ========== PENALITES ========== */
    @GetMapping("/penalites")
    public String listePenalites(
        @RequestParam(required = false) Boolean payee,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var penalites = payee != null
            ? penaliteRepo.findByPayee(payee, PageRequest.of(page, 20, Sort.by("dateCreation").descending()))
            : penaliteRepo.findAll(PageRequest.of(page, 20, Sort.by("dateCreation").descending()));

        model.addAttribute("penalites", penalites);
        model.addAttribute("payeeFiltre", payee);
        model.addAttribute("pageActuelle", page);

        return "bibliothecaire/penalites";
    }

    @PostMapping("/penalites/{penaliteId}/payer")
    public String payerPenalite(
        @PathVariable Long penaliteId,
        RedirectAttributes ra
    ) {
        try {
            var penalite = penaliteRepo.findById(penaliteId)
                .orElseThrow(() -> new com.unz.bibliotheque.exception.ResourceNotFoundException("Pénalité introuvable"));
            penalite.marquerPayee();
            penaliteRepo.save(penalite);
            ra.addFlashAttribute("success", "Pénalité de " + penalite.getMontant() + " FCFA payée avec succès.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/bibliothecaire/penalites";
    }
}
