package com.unz.bibliotheque.service;

import com.unz.bibliotheque.exception.*;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.*;
import com.unz.bibliotheque.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unz.bibliotheque.event.ExemplaireRetourneEvent;
import java.util.List;

/**
 * Service métier pour la gestion des réservations.
 *
 * Gère la file FIFO (Premier Arrivé, Premier Servi) des réservations.
 * Quand un exemplaire est rendu, le prochain étudiant dans la file
 * est notifié automatiquement via le Pattern Observer.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final OuvrageRepository     ouvrageRepo;
    private final EtudiantRepository    etudiantRepo;
    private final ExemplaireRepository  exemplaireRepo;
    private final ConfigurationService  configService;
    private final NotificationService   notificationService;

    /**
     * Crée une réservation pour un ouvrage indisponible.
     *
     * Règles :
     *   - L'ouvrage doit être indisponible (sinon emprunter directement)
     *   - L'étudiant ne doit pas déjà avoir une réservation active pour cet ouvrage
     *   - Le quota de réservations simultanées ne doit pas être dépassé
     *
     * @param etudiantId identifiant de l'étudiant
     * @param ouvrageId  identifiant de l'ouvrage à réserver
     * @return la réservation créée
     */
    public Reservation creerReservation(Long etudiantId, Long ouvrageId) {
        Ouvrage ouvrage = ouvrageRepo.findById(ouvrageId)
            .orElseThrow(() -> new ResourceNotFoundException("Ouvrage introuvable."));

        Etudiant etudiant = etudiantRepo.findById(etudiantId)
            .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable."));

        // Vérifier que l'ouvrage est bien indisponible
        if (ouvrage.estDisponible()) {
            throw new BusinessException(
                ouvrage.getNombreExemplairesDisponibles() + " exemplaire(s) disponible(s). "
                + "Empruntez directement plutôt que de réserver !"
            );
        }

        // Vérifier l'absence de doublon de réservation
        if (reservationRepo.countActiveByEtudiantAndOuvrage(etudiantId, ouvrageId) > 0) {
            throw new BusinessException("Vous avez déjà une réservation active pour cet ouvrage.");
        }

        // Vérifier le quota de réservations
        int maxReserv = configService.getMaxReservationsSimultanes();
        long nbActives = reservationRepo.findByEtudiantIdAndStatutIn(
            etudiantId,
            List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE)
        ).size();

        if (nbActives >= maxReserv) {
            throw new BusinessException(
                "Quota de réservations atteint (" + nbActives + "/" + maxReserv + ")."
            );
        }

        Reservation reservation = new Reservation();
        reservation.setEtudiant(etudiant);
        reservation.setOuvrage(ouvrage);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        Reservation saved = reservationRepo.save(reservation);
        log.info("Réservation créée : étudiant={}, ouvrage={}", etudiantId, ouvrageId);
        return saved;
    }

    /**
     * Annule une réservation existante.
     * L'étudiant ne peut annuler que ses propres réservations.
     *
     * @param reservationId identifiant de la réservation
     * @param etudiantId    identifiant de l'étudiant demandeur
     */
    public void annulerReservation(Long reservationId, Long etudiantId) {
        Reservation r = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        if (!r.getEtudiant().getId().equals(etudiantId)) {
            throw new UnauthorizedException("Vous ne pouvez annuler que vos propres réservations.");
        }

        if (r.getStatut() != StatutReservation.EN_ATTENTE
            && r.getStatut() != StatutReservation.CONFIRMEE) {
            throw new BusinessException("Cette réservation ne peut plus être annulée.");
        }

        r.annuler();
        reservationRepo.save(r);
        log.info("Réservation annulée : {}", reservationId);
    }

    /**
     * Passe à la réservation suivante dans la file FIFO.
     * Appelé automatiquement via le Pattern Observer lors d'un retour.
     *
     * Logique FIFO :
     *   1. Trouver la prochaine réservation EN_ATTENTE (triée par date)
     *   2. Trouver un exemplaire disponible
     *   3. Confirmer la réservation et réserver l'exemplaire
     *   4. Notifier l'étudiant par email
     *
     * @param ouvrage l'ouvrage dont un exemplaire vient d'être libéré
     */
    @EventListener
    public void onExemplaireRetourne(ExemplaireRetourneEvent event) {
        Ouvrage ouvrage = event.getEmprunt().getExemplaire().getOuvrage();
        passerAuSuivant(ouvrage);
    }

    /**
     * Trouve et notifie le prochain étudiant dans la file FIFO pour un ouvrage.
     *
     * @param ouvrage l'ouvrage rendu
     */
    public void passerAuSuivant(Ouvrage ouvrage) {
        reservationRepo.findProchainEnAttenteByOuvrageId(ouvrage.getId())
            .ifPresent(prochaine -> {
                Exemplaire exemplaire = ouvrage.getPremierExemplaireDisponible();
                if (exemplaire != null) {
                    int delaiHeures = configService.getDureeReservationHeures();
                    prochaine.confirmer(delaiHeures);
                    reservationRepo.save(prochaine);

                    exemplaire.marquerReserve();
                    exemplaireRepo.save(exemplaire);

                    // Notifier l'étudiant
                    notificationService.envoyerDisponibiliteReservation(prochaine);
                    log.info("File FIFO : réservation {} confirmée pour l'étudiant {}",
                        prochaine.getId(), prochaine.getEtudiant().getId());
                }
            });
    }

    /**
     * Retourne les réservations actives d'un étudiant.
     *
     * @param etudiantId identifiant de l'étudiant
     * @return liste des réservations EN_ATTENTE ou CONFIRMEE
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsActives(Long etudiantId) {
        return reservationRepo.findByEtudiantIdAndStatutIn(
            etudiantId,
            List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE)
        );
    }
}
