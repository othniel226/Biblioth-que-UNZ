package com.unz.bibliotheque.service;

import com.unz.bibliotheque.event.EmpruntCreeEvent;
import com.unz.bibliotheque.event.EmpruntProlongeEvent;
import com.unz.bibliotheque.event.ExemplaireRetourneEvent;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.TypeNotification;
import com.unz.bibliotheque.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des notifications par email.
 *
 * Design Pattern : Observer (Pattern Observer via @EventListener de Spring).
 * Ce service écoute les événements publiés par EmpruntService et
 * envoie les emails correspondants de manière asynchrone (@Async).
 *
 * Avantage du découplage Observer :
 *   EmpruntService publie un événement → NotificationService l'écoute
 *   EmpruntService ne dépend PAS de NotificationService (couplage minimal).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notifRepo;

    // ══════════════════════════════════════════════════════
    // LISTENERS — Pattern Observer
    // ══════════════════════════════════════════════════════

    /**
     * Écoute l'événement EmpruntCreeEvent et envoie un email de confirmation.
     * Exécuté de manière asynchrone pour ne pas bloquer la requête principale.
     *
     * @param event événement de création d'emprunt
     */
    @EventListener
    @Async
    public void onEmpruntCree(EmpruntCreeEvent event) {
        Emprunt emprunt = event.getEmprunt();
        envoyerConfirmationEmprunt(emprunt);
    }

    /**
     * Écoute l'événement ExemplaireRetourneEvent et envoie l'email de retour.
     *
     * @param event événement de retour d'exemplaire
     */
    @EventListener
    @Async
    public void onExemplaireRetourne(ExemplaireRetourneEvent event) {
        Emprunt emprunt = event.getEmprunt();
        envoyerConfirmationRetour(emprunt);
    }

    /**
     * Écoute l'événement EmpruntProlongeEvent et envoie l'email de prolongation.
     *
     * @param event événement de prolongation d'emprunt
     */
    @EventListener
    @Async
    public void onEmpruntProlonge(EmpruntProlongeEvent event) {
        Emprunt emprunt = event.getEmprunt();
        envoyerConfirmationProlongation(emprunt);
    }

    // ══════════════════════════════════════════════════════
    // MÉTHODES D'ENVOI D'EMAILS
    // ══════════════════════════════════════════════════════

    /**
     * Envoie un email de confirmation lors de la création d'un emprunt.
     *
     * @param emprunt l'emprunt nouvellement créé
     */
    @Transactional
    public void envoyerConfirmationEmprunt(Emprunt emprunt) {
        String sujet = "Confirmation d'emprunt — " + emprunt.getExemplaire().getOuvrage().getTitre();
        String message = String.format(
            "Bonjour %s,%n%nVotre emprunt a été enregistré avec succès.%n" +
            "Ouvrage : %s%nDate de retour prévue : %s%n%n" +
            "Merci de respecter la date de retour.%n%nBibliothèque UNZ",
            emprunt.getEtudiant().getNomComplet(),
            emprunt.getExemplaire().getOuvrage().getTitre(),
            emprunt.getDateRetourPrevu()
        );
        envoyerEmail(emprunt.getEtudiant(), TypeNotification.CONFIRMATION_EMPRUNT, sujet, message);
    }

    /**
     * Envoie un email de confirmation de retour avec le montant de pénalité.
     *
     * @param emprunt l'emprunt rendu
     */
    @Transactional
    public void envoyerConfirmationRetour(Emprunt emprunt) {
        String sujet = "Retour enregistré — " + emprunt.getExemplaire().getOuvrage().getTitre();
        String pénalité = emprunt.getPenalite() != null
            ? "\nPénalité de retard : " + emprunt.getPenalite().getMontant() + " FCFA"
            : "";
        String message = String.format(
            "Bonjour %s,%n%nLe retour de l'ouvrage '%s' a été enregistré.%s%n%nMerci.%n%nBibliothèque UNZ",
            emprunt.getEtudiant().getNomComplet(),
            emprunt.getExemplaire().getOuvrage().getTitre(),
            pénalité
        );
        envoyerEmail(emprunt.getEtudiant(), TypeNotification.CONFIRMATION_EMPRUNT, sujet, message);
    }

    /**
     * Envoie un email de rappel avant la date de retour.
     * Appelé par le scheduler RappelRetourScheduler.
     *
     * @param emprunt emprunt dont le retour approche
     */
    @Transactional
    public void envoyerRappelRetour(Emprunt emprunt) {
        String sujet = "Rappel : retour à prévoir — " + emprunt.getExemplaire().getOuvrage().getTitre();
        String message = String.format(
            "Bonjour %s,%n%nRappel : l'ouvrage '%s' doit être retourné le %s.%n" +
            "Merci de le ramener à temps pour éviter des pénalités.%n%nBibliothèque UNZ",
            emprunt.getEtudiant().getNomComplet(),
            emprunt.getExemplaire().getOuvrage().getTitre(),
            emprunt.getDateRetourPrevu()
        );
        envoyerEmail(emprunt.getEtudiant(), TypeNotification.RAPPEL_RETOUR, sujet, message);
    }

    /**
     * Envoie une alerte de retard avec le montant de pénalité.
     * Appelé par le scheduler VerificationRetardScheduler.
     *
     * @param emprunt emprunt en retard
     */
    @Transactional
    public void envoyerAlerteRetard(Emprunt emprunt) {
        String sujet = "URGENT : retard de retour — " + emprunt.getExemplaire().getOuvrage().getTitre();
        String message = String.format(
            "Bonjour %s,%n%nATTENTION : L'ouvrage '%s' aurait dû être retourné le %s.%n" +
            "Vous êtes en retard de %d jour(s).%n" +
            "Pénalité en cours : 100 FCFA/jour.%n%n" +
            "Veuillez retourner l'ouvrage dès que possible.%n%nBibliothèque UNZ",
            emprunt.getEtudiant().getNomComplet(),
            emprunt.getExemplaire().getOuvrage().getTitre(),
            emprunt.getDateRetourPrevu(),
            emprunt.getNombreJoursRetard()
        );
        envoyerEmail(emprunt.getEtudiant(), TypeNotification.ALERTE_RETARD, sujet, message);
    }

    /**
     * Notifie un étudiant que sa réservation est disponible.
     *
     * @param reservation la réservation confirmée
     */
    @Transactional
    public void envoyerDisponibiliteReservation(Reservation reservation) {
        String sujet = "Votre réservation est disponible — " + reservation.getOuvrage().getTitre();
        String message = String.format(
            "Bonjour %s,%n%nBonne nouvelle ! L'ouvrage '%s' que vous avez réservé est maintenant disponible.%n" +
            "Vous avez 48 heures pour venir le récupérer à la bibliothèque.%n%n" +
            "Passé ce délai, votre réservation sera annulée.%n%nBibliothèque UNZ",
            reservation.getEtudiant().getNomComplet(),
            reservation.getOuvrage().getTitre()
        );
        envoyerEmail(reservation.getEtudiant(), TypeNotification.RESERVATION_DISPO, sujet, message);
    }

    /**
     * Envoie un email de confirmation de prolongation.
     *
     * @param emprunt l'emprunt prolongé
     */
    @Transactional
    public void envoyerConfirmationProlongation(Emprunt emprunt) {
        String sujet = "Prolongation accordée — " + emprunt.getExemplaire().getOuvrage().getTitre();
        String message = String.format(
            "Bonjour %s,%n%nVotre prolongation a été accordée.%n" +
            "Nouvelle date de retour : %s%n%nBibliothèque UNZ",
            emprunt.getEtudiant().getNomComplet(),
            emprunt.getDateRetourPrevu()
        );
        envoyerEmail(emprunt.getEtudiant(), TypeNotification.PROLONGATION_ACCORD, sujet, message);
    }

    // ══════════════════════════════════════════════════════
    // MÉTHODE INTERNE
    // ══════════════════════════════════════════════════════

    /**
     * Envoie un email et sauvegarde la notification en base de données.
     *
     * @param destinataire utilisateur destinataire
     * @param type         type de notification
     * @param sujet        sujet de l'email
     * @param message      corps du message
     */
    private void envoyerEmail(Utilisateur destinataire, TypeNotification type,
                              String sujet, String message) {
        try {
            // Envoi de l'email via JavaMailSender
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinataire.getEmail());
            email.setSubject("[Bibliothèque UNZ] " + sujet);
            email.setText(message);
            email.setFrom("bibliotheque.unz@gmail.com");
            mailSender.send(email);
            log.info("Email envoyé à {} : {}", destinataire.getEmail(), sujet);
        } catch (Exception e) {
            // L'envoi d'email ne doit jamais bloquer l'opération principale
            log.error("Échec d'envoi d'email à {} : {}", destinataire.getEmail(), e.getMessage());
        }

        // Sauvegarde de la notification en base (pour l'historique in-app)
        Notification notif = new Notification();
        notif.setDestinataire(destinataire);
        notif.setType(type);
        notif.setSujet(sujet);
        notif.setMessage(message);
        notifRepo.save(notif);
    }
}
