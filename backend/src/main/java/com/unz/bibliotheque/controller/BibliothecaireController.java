package com.unz.bibliotheque.controller;

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

@Controller
@RequestMapping("/bibliothecaire")
@RequiredArgsConstructor
public class BibliothecaireController {

    private final EmpruntRepository empruntRepo;
    private final ExemplaireRepository exemplaireRepo;
    private final PenaliteRepository penaliteRepo;
    private final OuvrageRepository ouvrageRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final EmpruntService empruntService;

    /* ========== TABLEAU DE BORD ========== */
    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(Model model) {
        model.addAttribute("nbEmpruntsEnCours",
            empruntRepo.countByStatut(StatutEmprunt.EN_COURS));
        model.addAttribute("nbEmpruntsEnRetard",
            empruntRepo.countByStatut(StatutEmprunt.EN_RETARD));
        model.addAttribute("nbPenalitesImpayees",
            penaliteRepo.countByPayeeFalse());
        model.addAttribute("nbOuvrages", ouvrageRepo.count());
        return "bibliothecaire/tableau-de-bord";
    }

    /* ========== RETOURS ========== */
    @GetMapping("/retours")
    public String afficherRetours(Model model) {
        var derniersRetours = empruntRepo.findByStatutOrderByDateRetourReelDesc(
            StatutEmprunt.RENDU,
            PageRequest.of(0, 10)
        );
        model.addAttribute("derniersRetours", derniersRetours);
        return "bibliothecaire/retours";
    }

    @PostMapping("/retours")
    public String rechercherParCodeBarres(
        @RequestParam String codeBarres,
        Model model,
        RedirectAttributes ra
    ) {
        // Chercher l'exemplaire par code-barres
        var exemplaireOpt = exemplaireRepo.findByCodeBarres(codeBarres.trim().toUpperCase());

        if (exemplaireOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Aucun exemplaire trouvé avec le code-barres : " + codeBarres);
            return "redirect:/bibliothecaire/retours";
        }

        var exemplaire = exemplaireOpt.get();

        // Chercher l'emprunt actif pour cet exemplaire
        var empruntOpt = empruntRepo.findEmpruntActifByExemplaireId(exemplaire.getId());

        if (empruntOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Aucun emprunt actif trouvé pour cet exemplaire.");
            return "redirect:/bibliothecaire/retours";
        }

        var emprunt = empruntOpt.get();

        // Calculer la pénalité potentielle (sans l'enregistrer encore)
        model.addAttribute("emprunt", emprunt);
        model.addAttribute("penalite", emprunt.getPenalite());

        var derniersRetours = empruntRepo.findByStatutOrderByDateRetourReelDesc(
            StatutEmprunt.RENDU,
            PageRequest.of(0, 10)
        );
        model.addAttribute("derniersRetours", derniersRetours);

        return "bibliothecaire/retours";
    }

    @PostMapping("/retours/valider")
    public String validerRetour(
        @RequestParam Long empruntId,
        @RequestParam(required = false) String codeBarres,
        RedirectAttributes ra
    ) {
        try {
            empruntService.enregistrerRetour(empruntId);
            ra.addFlashAttribute("success", "Retour enregistré avec succès !");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur lors du retour : " + e.getMessage());
        }
        return "redirect:/bibliothecaire/retours";
    }

    /* ========== PÉNALITÉS ========== */
    @GetMapping("/penalites")
    public String afficherPenalites(
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var penalites = penaliteRepo.findByPayeeFalseOrderByDateCreationDesc(
            PageRequest.of(page, 20)
        );
        model.addAttribute("penalites", penalites);
        model.addAttribute("pageActuelle", page);
        model.addAttribute("totalImpayees", penaliteRepo.countByPayeeFalse());
        return "bibliothecaire/penalites";
    }

    @PostMapping("/penalites/{id}/payer")
    public String encaisserPenalite(
        @PathVariable Long id,
        RedirectAttributes ra
    ) {
        try {
            var penalite = penaliteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pénalité introuvable"));
            penalite.marquerPayee();
            penaliteRepo.save(penalite);
            ra.addFlashAttribute("success", "Pénalité encaissée : " + penalite.getMontant() + " FCFA");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bibliothecaire/penalites";
    }

    /* ========== CATALOGUE ========== */
    @GetMapping("/catalogue")
    public String catalogue(
        @RequestParam(required = false) String q,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var ouvrages = (q != null && !q.isBlank())
            ? ouvrageRepo.findByTitreContainingIgnoreCaseOrAuteurContainingIgnoreCase(
                q, q, PageRequest.of(page, 20))
            : ouvrageRepo.findAll(PageRequest.of(page, 20));

        model.addAttribute("ouvrages", ouvrages);
        model.addAttribute("q", q);
        model.addAttribute("pageActuelle", page);
        return "bibliothecaire/catalogue";
    }

    /* ========== NOUVEL EMPRUNT MANUEL ========== */
    @GetMapping("/emprunts/nouveau")
    public String afficherNouvelEmprunt(Model model) {
        model.addAttribute("etudiants",
            utilisateurRepo.findAllEtudiants());
        model.addAttribute("exemplaires",
            exemplaireRepo.findByStatutDisponible());
        return "bibliothecaire/nouvel-emprunt";
    }

    @PostMapping("/emprunts/nouveau")
    public String creerEmpruntManuel(
        @RequestParam Long etudiantId,
        @RequestParam Long exemplaireId,
        RedirectAttributes ra
    ) {
        try {
            empruntService.creerEmprunt(etudiantId, exemplaireId);
            ra.addFlashAttribute("success", "Emprunt enregistré avec succès !");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bibliothecaire/tableau-de-bord";
    }
}
