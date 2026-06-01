package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.model.enums.StatutReservation;
import com.unz.bibliotheque.repository.*;
import com.unz.bibliotheque.service.EmpruntService;
import com.unz.bibliotheque.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class EtudiantController {

    private final UtilisateurRepository utilisateurRepo;
    private final EmpruntRepository empruntRepo;
    private final ReservationRepository reservationRepo;
    private final PenaliteRepository penaliteRepo;
    private final EmpruntService empruntService;
    private final ReservationService reservationService;
    private final OuvrageRepository ouvrageRepo;
    private final ExemplaireRepository exemplaireRepo;

    private Etudiant getEtudiant(Authentication auth) {
        return (Etudiant) utilisateurRepo.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));
    }

    /* ========== TABLEAU DE BORD ========== */
    @GetMapping("/tableau-de-bord")
    @Transactional(readOnly = true)
    public String tableauDeBord(Authentication auth, Model model) {
        Etudiant etudiant = getEtudiant(auth);

        var empruntsEnCours = empruntRepo.findEmpruntsActifsByEtudiant(etudiant.getId());
        var reservations = reservationRepo.findByEtudiantIdAndStatutIn(
            etudiant.getId(),
            List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE)
        );

        // Forcer le chargement des relations lazy dans la transaction
        empruntsEnCours.forEach(e -> {
            if (e.getExemplaire() != null && e.getExemplaire().getOuvrage() != null) {
                e.getExemplaire().getOuvrage().getTitre();
            }
        });
        reservations.forEach(r -> {
            if (r.getOuvrage() != null) {
                r.getOuvrage().getTitre();
            }
        });

        BigDecimal penalites = penaliteRepo.sumMontantImpayeByEtudiantId(etudiant.getId());
        long nbRetards = empruntsEnCours.stream()
            .filter(e -> e.getStatut() == StatutEmprunt.EN_RETARD || e.estEnRetard())
            .count();
        long totalEmprunts = empruntRepo.countByEtudiantId(etudiant.getId());

        model.addAttribute("etudiant", etudiant);
        model.addAttribute("empruntsEnCours", empruntsEnCours);
        model.addAttribute("reservations", reservations);
        model.addAttribute("penalitesImpayees", penalites != null ? penalites : BigDecimal.ZERO);
        model.addAttribute("nbEmpruntsEnCours", empruntsEnCours.size());
        model.addAttribute("nbRetards", nbRetards);
        model.addAttribute("nbReservations", reservations.size());
        model.addAttribute("totalEmprunts", totalEmprunts);

        return "etudiant/tableau-de-bord";
    }

    /* ========== HISTORIQUE ========== */
    @GetMapping("/historique")
    @Transactional(readOnly = true)
    public String historique(
        Authentication auth,
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        Etudiant etudiant = getEtudiant(auth);
        var historique = empruntRepo.findHistoriqueByEtudiant(
            etudiant.getId(),
            PageRequest.of(page, 10, Sort.by("dateEmprunt").descending())
        );
        // Forcer le chargement lazy
        historique.forEach(e -> {
            if (e.getExemplaire() != null && e.getExemplaire().getOuvrage() != null) {
                e.getExemplaire().getOuvrage().getTitre();
            }
        });
        model.addAttribute("etudiant", etudiant);
        model.addAttribute("historique", historique);
        model.addAttribute("pageActuelle", page);
        return "etudiant/historique";
    }

    /* ========== PROLONGER UN EMPRUNT ========== */
    @PostMapping("/emprunts/{id}/prolonger")
    public String prolongerEmprunt(
        @PathVariable Long id,
        Authentication auth,
        RedirectAttributes ra
    ) {
        try {
            Etudiant etudiant = getEtudiant(auth);
            empruntService.prolongerEmprunt(id, etudiant.getId());
            ra.addFlashAttribute("success", "Emprunt prolongé avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/etudiant/tableau-de-bord";
    }

    /* ========== RÉSERVER UN OUVRAGE ========== */
    @PostMapping("/reservations")
    public String creerReservation(
        @RequestParam Long ouvrageId,
        Authentication auth,
        RedirectAttributes ra
    ) {
        try {
            Etudiant etudiant = getEtudiant(auth);
            reservationService.creerReservation(etudiant.getId(), ouvrageId);
            ra.addFlashAttribute("success", "Réservation effectuée avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/catalogue";
    }

    /* ========== ANNULER UNE RÉSERVATION ========== */
    @PostMapping("/reservations/{id}/annuler")
    public String annulerReservation(
        @PathVariable Long id,
        Authentication auth,
        RedirectAttributes ra
    ) {
        try {
            Etudiant etudiant = getEtudiant(auth);
            reservationService.annulerReservation(id, etudiant.getId());
            ra.addFlashAttribute("success", "Réservation annulée.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/etudiant/tableau-de-bord";
    }
}
