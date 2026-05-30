package com.unz.bibliotheque.event;

import com.unz.bibliotheque.model.Emprunt;
import org.springframework.context.ApplicationEvent;

/**
 * Événement Spring publié lors de la prolongation d'un emprunt.
 *
 * Design Pattern : Observer (via ApplicationEventPublisher de Spring)
 *
 * Déclenché par : EmpruntService.prolongerEmprunt()
 * Écouté par    : NotificationService (email de confirmation de prolongation)
 */
public class EmpruntProlongeEvent extends ApplicationEvent {

    /** L'emprunt qui vient d'être prolongé */
    private final Emprunt emprunt;

    /**
     * Constructeur de l'événement.
     *
     * @param source  objet source
     * @param emprunt l'emprunt prolongé
     */
    public EmpruntProlongeEvent(Object source, Emprunt emprunt) {
        super(source);
        this.emprunt = emprunt;
    }

    /**
     * Retourne l'emprunt prolongé.
     *
     * @return l'emprunt concerné
     */
    public Emprunt getEmprunt() {
        return emprunt;
    }
}
