package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.repository.CategorieRepository;
import com.unz.bibliotheque.repository.OuvrageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur du catalogue public d'ouvrages.
 *
 * Accessible sans authentification (configuré dans SecurityConfig).
 * Permet la recherche et la consultation des fiches d'ouvrages.
 *
 * US-02 : Rechercher un ouvrage
 * US-03 : Consulter la fiche d'un ouvrage
 */
@Controller
@RequestMapping("/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final OuvrageRepository   ouvrageRepo;
    private final CategorieRepository categorieRepo;

    /**
     * Affiche le catalogue avec recherche et filtres optionnels.
     *
     * @param q           terme de recherche (titre, auteur, ISBN) — optionnel
     * @param categorieId filtre par catégorie — optionnel
     * @param disponible  filtre par disponibilité — optionnel
     * @param page        numéro de page (0-indexé)
     * @param model       modèle Thymeleaf
     * @return template catalogue/index
     */
    @GetMapping
    public String catalogue(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Long categorieId,
        @RequestParam(required = false) Boolean disponible,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        // Recherche paginée avec 12 ouvrages par page, triés par titre
        var ouvrages = ouvrageRepo.rechercher(
            q, categorieId, disponible,
            PageRequest.of(page, 12, Sort.by("titre"))
        );

        model.addAttribute("ouvrages",    ouvrages);
        model.addAttribute("categories", categorieRepo.findAll());
        model.addAttribute("q",          q);
        model.addAttribute("categorieId", categorieId);
        model.addAttribute("disponible",  disponible);
        model.addAttribute("pageActuelle", page);

        return "catalogue/index";
    }

    /**
     * Affiche la fiche détaillée d'un ouvrage.
     *
     * @param id    identifiant de l'ouvrage
     * @param model modèle Thymeleaf
     * @return template catalogue/fiche
     */
    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        var ouvrage = ouvrageRepo.findById(id)
            .orElseThrow(() -> new com.unz.bibliotheque.exception.ResourceNotFoundException(
                "Ouvrage introuvable (id=" + id + ")"
            ));

        model.addAttribute("ouvrage", ouvrage);
        return "catalogue/fiche";
    }
}
