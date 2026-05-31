package com.unz.bibliotheque.event;

import com.unz.bibliotheque.model.Emprunt;
import org.springframework.context.ApplicationEvent;

/**
 * Événement Spring publié lors du retour d'un exemplaire.
 *
 * Design Pattern : Observer (via ApplicationEventPublisher de Spring)
 *
 * Déclenché par : EmpruntService.enregistrerRetour()
 * Écouté par    :
 *   - NotificationService (email de confirmation de retour)
 *   - ReservationService (notification du prochain dans la file FIFO)
 *
 * Cet événement déclenche la gestion de la file FIFO des réservations :
 * si un étudiant attend cet ouvrage, il est notifié automatiquement.
 */
public class ExemplaireRetourneEvent extends ApplicationEvent {

    /** L'emprunt qui vient d'être clôturé */
    private final Emprunt emprunt;

    /**
     * Constructeur de l'événement.
     *
     * @param source  objet source (généralement EmpruntService)
     * @param emprunt l'emprunt clôturé
     */
    public ExemplaireRetourneEvent(Object source, Emprunt emprunt) {
        super(source);
        this.emprunt = emprunt;
    }

    /**
     * Retourne l'emprunt clôturé associé à cet événement.
     *
     * @return l'emprunt rendu
     */
    public Emprunt getEmprunt() {
        return emprunt;
    }
}
