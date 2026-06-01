package com.unz.bibliotheque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée quand une ressource demandée n'est pas trouvée.
 * Retourne HTTP 404 (Not Found) au client.
 *
 * Exemples : étudiant introuvable, ouvrage inexistant, emprunt non trouvé.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crée une exception de ressource non trouvée.
     *
     * @param message description de la ressource non trouvée
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
