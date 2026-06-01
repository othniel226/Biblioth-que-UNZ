package com.unz.bibliotheque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée lors de la violation d'une règle métier.
 * Retourne HTTP 400 (Bad Request) au client.
 *
 * Exemples d'utilisation :
 *   - Quota d'emprunts atteint (max 3)
 *   - Compte bloqué (pénalités impayées ≥ seuil)
 *   - Exemplaire non disponible
 *   - Prolongation déjà effectuée
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    /**
     * Crée une exception métier avec un message explicatif.
     *
     * @param message description de la règle métier violée
     */
    public BusinessException(String message) {
        super(message);
    }
}
