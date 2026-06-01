package com.unz.bibliotheque.service;

import com.unz.bibliotheque.event.EmpruntCreeEvent;
import com.unz.bibliotheque.event.EmpruntProlongeEvent;
import com.unz.bibliotheque.event.ExemplaireRetourneEvent;
import com.unz.bibliotheque.exception.BusinessException;
import com.unz.bibliotheque.exception.ResourceNotFoundException;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.pattern.strategy.PenaliteStrategy;
import com.unz.bibliotheque.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier principal pour la gestion des emprunts.
 *
 * Implémente les règles métier :
 *   Règle 1 : Le compte étudiant doit être actif
 *   Règle 2 : L'étudiant ne doit pas dépasser le quota (max 3 emprunts)
 *   Règle 3 : L'étudiant ne doit pas avoir de pénalités impayées >= seuil
 *   Règle 4 : L'exemplaire doit être DISPONIBLE (verrou pessimiste)
 *
 * Design Patterns utilisés :
 *   - Observer : publication d'événements Spring (EmpruntCreeEvent, etc.)
 *   - Strategy : calcul de pénalités (PenaliteStrategy)
 *   - Pessimistic Locking : verrou sur l'exemplaire pour éviter les doublons
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmpruntService {

    private final EmpruntRepository     empruntRepo;
    private final EtudiantRepository    etudiantRepo;
    private final ExemplaireRepository  exemplaireRepo;
    private final PenaliteRepository    penaliteRepo;
    private final ConfigurationService  configService;
    private final NotificationService   notificationService;
    private final ApplicationEventPublisher eventPublisher;

    /** Strategy de calcul des pénalités (TarifFixeStrategy par @Primary) */
    private final PenaliteStrategy penaliteStrategy;

    // ══════════════════════════════════════════════════════
    // CRÉER UN EMPRUNT (US-04 / US-10)
    // ══════════════════════════════════════════════════════

    /**
     * Crée un nouvel emprunt après vérification de toutes les règles métier.
     *
     * @param etudiantId   identifiant de l'étudiant
     * @param exemplaireId identifiant de l'exemplaire à emprunter
     * @return l'emprunt créé et persisté
     * @throws BusinessException         si une règle métier est violée
     * @throws ResourceNotFoundException si l'étudiant ou l'exemplaire est introuvable
     */
    public Emprunt creerEmprunt(Long etudiantId, Long exemplaireId) {
        // Récupérer l'étudiant
        Etudiant etudiant = etudiantRepo.findById(etudiantId)
            .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable (id=" + etudiantId + ")"));

        // Règle 1 : compte actif
        if (!etudiant.getActif()) {
            throw new BusinessException("Votre compte est désactivé. Contactez l'administrateur.");
        }

        // Règle 2 : vérifier le quota d'emprunts
        int maxEmprunts = configService.getMaxEmpruntsSimultanes();
        long nbEmpruntsActifs = empruntRepo.countEmpruntsActifsByEtudiant(etudiantId);
        if (nbEmpruntsActifs >= maxEmprunts) {
            throw new BusinessException(
                "Quota d'emprunts atteint (" + nbEmpruntsActifs + "/" + maxEmprunts + "). "
                + "Retournez un ouvrage avant d'en emprunter un nouveau."
            );
        }

        // Règle 3 : vérifier les pénalités impayées
        int seuilBlocage = configService.getSeuilBlocageFcfa();
        BigDecimal penalitesImpayees = penaliteRepo.sumMontantImpayeByEtudiantId(etudiantId);
        if (penalitesImpayees.compareTo(BigDecimal.valueOf(seuilBlocage)) >= 0) {
            throw new BusinessException(
                "Compte bloqué : " + penalitesImpayees + " FCFA de pénalités impayées. "
                + "Réglez vos pénalités avant d'emprunter."
            );
        }

        // Règle 4 : vérifier la disponibilité avec verrou pessimiste (Pattern Pessimistic Lock)
        Exemplaire exemplaire = exemplaireRepo.findByIdWithLock(exemplaireId)
            .orElseThrow(() -> new ResourceNotFoundException("Exemplaire introuvable (id=" + exemplaireId + ")"));

        if (!exemplaire.getStatut().name().equals("DISPONIBLE")) {
            throw new BusinessException(
                "Cet exemplaire n'est pas disponible (statut : " + exemplaire.getStatut() + ")."
            );
        }

        // Mettre à jour le statut de l'exemplaire
        exemplaire.marquerEmprunte();
        exemplaireRepo.save(exemplaire);

        // Calculer la date de retour prévue
        int dureeJours = configService.getDureeEmpruntJours();
        LocalDate dateRetour = LocalDate.now().plusDays(dureeJours);

        // Créer l'emprunt
        Emprunt emprunt = new Emprunt();
        emprunt.setEtudiant(etudiant);
        emprunt.setExemplaire(exemplaire);
        emprunt.setDateEmprunt(LocalDateTime.now());
        emprunt.setDateRetourPrevu(dateRetour);
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setProlonge(false);

        Emprunt empruntSauvegarde = empruntRepo.save(emprunt);

        // Pattern Observer : publier l'événement (NotificationService l'écoute)
        eventPublisher.publishEvent(new EmpruntCreeEvent(this, empruntSauvegarde));

        log.info("Emprunt créé : étudiant={}, exemplaire={}, retour={}",
            etudiantId, exemplaireId, dateRetour);

        return empruntSauvegarde;
    }

    // ══════════════════════════════════════════════════════
    // ENREGISTRER UN RETOUR (US-11) — BIBLIOTHÉCAIRE UNIQUEMENT
    // ══════════════════════════════════════════════════════

    /**
     * Enregistre le retour d'un ouvrage et calcule la pénalité si retard.
     * EXCLUSIVEMENT effectué par le bibliothécaire.
     *
     * @param empruntId identifiant de l'emprunt à clore
     * @return l'emprunt mis à jour
     * @throws ResourceNotFoundException si l'emprunt est introuvable
     * @throws BusinessException         si l'emprunt est déjà rendu
     */
    public Emprunt enregistrerRetour(Long empruntId) {
        Emprunt emprunt = empruntRepo.findById(empruntId)
            .orElseThrow(() -> new ResourceNotFoundException("Emprunt introuvable (id=" + empruntId + ")"));

        if (emprunt.getStatut() == StatutEmprunt.RENDU) {
            throw new BusinessException("Cet emprunt a déjà été clôturé.");
        }

        // Libérer l'exemplaire
        Exemplaire exemplaire = emprunt.getExemplaire();
        exemplaire.marquerDisponible();
        exemplaireRepo.save(exemplaire);

        // Clore l'emprunt
        emprunt.clore(LocalDate.now());

        // Calculer la pénalité si retard (Pattern Strategy)
        if (emprunt.estEnRetard() || emprunt.getNombreJoursRetard() > 0) {
            BigDecimal montant = penaliteStrategy.calculer(emprunt);
            if (montant.compareTo(BigDecimal.ZERO) > 0) {
                Penalite penalite = new Penalite();
                penalite.setEmprunt(emprunt);
                penalite.setMontant(montant);
                penalite.setJoursRetard(emprunt.getNombreJoursRetard());
                penalite.setStrategieUtilisee(penaliteStrategy.getDescription());
                penaliteRepo.save(penalite);
                log.info("Pénalité calculée : {} FCFA pour {} jours de retard",
                    montant, emprunt.getNombreJoursRetard());
            }
        }

        Emprunt empruntMisAJour = empruntRepo.save(emprunt);

        // Pattern Observer : notifier le prochain dans la file FIFO
        eventPublisher.publishEvent(new ExemplaireRetourneEvent(this, empruntMisAJour));

        log.info("Retour enregistré : empruntId={}", empruntId);
        return empruntMisAJour;
    }

    // ══════════════════════════════════════════════════════
    // PROLONGER UN EMPRUNT (US-06)
    // ══════════════════════════════════════════════════════

    /**
     * Prolonge un emprunt d'une durée supplémentaire.
     * Une seule prolongation est autorisée par emprunt.
     *
     * @param empruntId  identifiant de l'emprunt
     * @param etudiantId identifiant de l'étudiant demandeur (vérification)
     * @return l'emprunt prolongé
     * @throws BusinessException si la prolongation n'est pas autorisée
     */
    public Emprunt prolongerEmprunt(Long empruntId, Long etudiantId) {
        Emprunt emprunt = empruntRepo.findById(empruntId)
            .orElseThrow(() -> new ResourceNotFoundException("Emprunt introuvable."));

        // Vérifier que l'emprunt appartient à l'étudiant
        if (!emprunt.getEtudiant().getId().equals(etudiantId)) {
            throw new BusinessException("Cet emprunt ne vous appartient pas.");
        }

        // Vérifier si déjà prolongé
        if (emprunt.getProlonge()) {
            throw new BusinessException("Cet emprunt a déjà été prolongé. Une seule prolongation est autorisée.");
        }

        // Vérifier si l'emprunt est en retard
        if (emprunt.estEnRetard()) {
            throw new BusinessException("Impossible de prolonger un emprunt en retard.");
        }

        // Calculer la nouvelle date de retour
        int dureeJours = configService.getDureeEmpruntJours();
        emprunt.setDateRetourPrevu(emprunt.getDateRetourPrevu().plusDays(dureeJours));
        emprunt.setProlonge(true);
        emprunt.setStatut(StatutEmprunt.PROLONGE);

        Emprunt empruntProlonge = empruntRepo.save(emprunt);

        // Pattern Observer : notification de prolongation
        eventPublisher.publishEvent(new EmpruntProlongeEvent(this, empruntProlonge));

        log.info("Emprunt prolongé : empruntId={}, nouvelle date={}", empruntId, emprunt.getDateRetourPrevu());
        return empruntProlonge;
    }

    // ══════════════════════════════════════════════════════
    // LECTURES
    // ══════════════════════════════════════════════════════

    /**
     * Retourne les emprunts actifs d'un étudiant.
     *
     * @param etudiantId identifiant de l'étudiant
     * @return liste des emprunts EN_COURS, EN_RETARD ou PROLONGE
     */
    @Transactional(readOnly = true)
    public List<Emprunt> getEmpruntsEnCours(Long etudiantId) {
        return empruntRepo.findEmpruntsActifsByEtudiant(etudiantId);
    }

    /**
     * Retourne l'historique paginé des emprunts d'un étudiant.
     *
     * @param etudiantId identifiant de l'étudiant
     * @param pageable   paramètres de pagination
     * @return page d'emprunts
     */
    @Transactional(readOnly = true)
    public Page<Emprunt> getHistoriqueEtudiant(Long etudiantId, Pageable pageable) {
        return empruntRepo.findHistoriqueByEtudiant(etudiantId, pageable);
    }
}
