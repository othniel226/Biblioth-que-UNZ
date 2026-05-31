package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.Ouvrage;
import com.unz.bibliotheque.repository.OuvrageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour l'API catalogue.
 * 
 * Pattern : REST Controller (JSON)
 * URL de base : /api/catalogue
 */
@RestController
@RequestMapping("/api/catalogue")
@RequiredArgsConstructor
public class CatalogueRestController {

    private final OuvrageRepository ouvrageRepo;

    /**
     * Liste tous les ouvrages en JSON.
     * 
     * GET /api/catalogue/ouvrages
     */
    @GetMapping("/ouvrages")
    public List<Ouvrage> getAllOuvrages() {
        return ouvrageRepo.findAll();
    }
}
