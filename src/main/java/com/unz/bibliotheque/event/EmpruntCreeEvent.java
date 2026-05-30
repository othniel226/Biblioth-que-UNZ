package com.unz.bibliotheque.event;

import com.unz.bibliotheque.model.Emprunt;
import org.springframework.context.ApplicationEvent;

/**
 * Événement Spring publié lors de la création d'un emprunt.
 *
 * Design Pattern : Observer (via ApplicationEventPublisher de Spring)
 *
 * Déclenché par : EmpruntService.creerEmprunt()
 * Écouté par    : NotificationService (envoi email de confirmation)
 *
 * Avantage : EmpruntService ne connaît pas NotificationService.
 * Couplage minimal entre les composants (principe Open/Closed).
 */
public class EmpruntCreeEvent extends ApplicationEvent {

    /** L'emprunt qui vient d'être créé */
    private final Emprunt emprunt;

    /**
     * Constructeur de l'événement.
     *
     * @param source  objet source de l'événement (généralement EmpruntService)
     * @param emprunt l'emprunt nouvellement créé
     */
    public EmpruntCreeEvent(Object source, Emprunt emprunt) {
        super(source);
        this.emprunt = emprunt;
    }

    /**
     * Retourne l'emprunt associé à cet événement.
     *
     * @return l'emprunt créé
     */
    public Emprunt getEmprunt() {
        return emprunt;
    }
}
