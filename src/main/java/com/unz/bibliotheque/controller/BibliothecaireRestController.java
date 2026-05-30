package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.dto.request.EmpruntRequest;
import com.unz.bibliotheque.dto.response.DashboardStatsResponse;
import com.unz.bibliotheque.dto.response.EmpruntResponse;
import com.unz.bibliotheque.dto.response.PenaliteResponse;
import com.unz.bibliotheque.mapper.EmpruntMapper;
import com.unz.bibliotheque.mapper.PenaliteMapper;
import com.unz.bibliotheque.model.enums.Role;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.repository.*;
import com.unz.bibliotheque.service.EmpruntService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour l'API bibliothécaire.
 *
 * Pattern : REST Controller (JSON)
 * URL de base : /api/bibliothecaire
 *
 * Accessible uniquement aux rôles BIBLIOTHECAIRE et ADMINISTRATEUR.
 */
@RestController
@RequestMapping("/api/bibliothecaire")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BIBLIOTHECAIRE', 'ADMINISTRATEUR')")
public class BibliothecaireRestController {

    private final EmpruntRepository empruntRepo;
    private final OuvrageRepository ouvrageRepo;
    private final EtudiantRepository etudiantRepo;
    private final ReservationRepository reservationRepo;
    private final ExemplaireRepository exemplaireRepo;
    private final PenaliteRepository penaliteRepo;
    private final UtilisateurRepository utilisateurRepo;

    private final EmpruntService empruntService;
    private final EmpruntMapper empruntMapper;
    private final PenaliteMapper penaliteMapper;

    /* ========== STATS TABLEAU DE BORD ========== */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        var stats = DashboardStatsResponse.builder()
            .nbUtilisateurs(utilisateurRepo.count())
            .nbEtudiants(utilisateurRepo.countByRole(Role.ETUDIANT))
            .nbBibliothecaires(utilisateurRepo.countByRole(Role.BIBLIOTHECAIRE))
            .nbAdministrateurs(utilisateurRepo.countByRole(Role.ADMINISTRATEUR))
            .nbEmpruntsTotal(empruntRepo.count())
            .nbEmpruntsEnCours(empruntRepo.countByStatut(StatutEmprunt.EN_COURS))
            .nbEmpruntsEnRetard(empruntRepo.countByStatut(StatutEmprunt.EN_RETARD))
            .nbOuvrages(ouvrageRepo.count())
            .nbExemplaires(exemplaireRepo.count())
            .nbExemplairesDisponibles(exemplaireRepo.countByStatut(com.unz.bibliotheque.model.enums.StatutExemplaire.DISPONIBLE))
            .nbReservationsEnAttente(reservationRepo.countByStatut(com.unz.bibliotheque.model.enums.StatutReservation.EN_ATTENTE))
            .nbPenalitesImpayees(penaliteRepo.countByPayeeFalse())
            .build();

        return ResponseEntity.ok(stats);
    }

    /* ========== EMPRUNTS ========== */
    @GetMapping("/emprunts")
    public ResponseEntity<List<EmpruntResponse>> getEmprunts(
        @RequestParam(required = false) StatutEmprunt statut,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        var emprunts = statut != null
            ? empruntRepo.findByStatut(statut, PageRequest.of(page, size, Sort.by("dateEmprunt").descending()))
            : empruntRepo.findAll(PageRequest.of(page, size, Sort.by("dateEmprunt").descending()));

        var responses = emprunts.getContent().stream()
            .map(empruntMapper::toResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/emprunts")
    public ResponseEntity<EmpruntResponse> creerEmprunt(@RequestBody EmpruntRequest request) {
        var emprunt = empruntService.creerEmprunt(request.getEtudiantId(), request.getExemplaireId());
        return ResponseEntity.ok(empruntMapper.toResponse(emprunt));
    }

    /* ========== RETOURS ========== */
    @PostMapping("/retours/{empruntId}")
    public ResponseEntity<EmpruntResponse> enregistrerRetour(@PathVariable Long empruntId) {
        var emprunt = empruntService.enregistrerRetour(empruntId);
        return ResponseEntity.ok(empruntMapper.toResponse(emprunt));
    }

    /* ========== PENALITES ========== */
    @GetMapping("/penalites")
    public ResponseEntity<List<PenaliteResponse>> getPenalites(
        @RequestParam(required = false) Boolean payee,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        var penalites = payee != null
            ? penaliteRepo.findByPayee(payee, PageRequest.of(page, size, Sort.by("dateCreation").descending()))
            : penaliteRepo.findAll(PageRequest.of(page, size, Sort.by("dateCreation").descending()));

        var responses = penalites.getContent().stream()
            .map(penaliteMapper::toResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/penalites/{penaliteId}/payer")
    public ResponseEntity<PenaliteResponse> payerPenalite(@PathVariable Long penaliteId) {
        var penalite = penaliteRepo.findById(penaliteId)
            .orElseThrow(() -> new com.unz.bibliotheque.exception.ResourceNotFoundException("Pénalité introuvable"));

        penalite.marquerPayee();
        var saved = penaliteRepo.save(penalite);

        return ResponseEntity.ok(penaliteMapper.toResponse(saved));
    }
}
