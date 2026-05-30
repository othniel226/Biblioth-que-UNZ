package com.unz.bibliotheque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée quand un utilisateur tente d'accéder à une ressource
 * qui ne lui appartient pas ou pour laquelle il n'a pas les droits.
 * Retourne HTTP 403 (Forbidden) au client.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    /**
     * Crée une exception d'accès non autorisé.
     *
     * @param message description de l'accès refusé
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
