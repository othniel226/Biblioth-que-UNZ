package com.unz.bibliotheque.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour toute l'application.
 *
 * Intercepte les exceptions levées dans les contrôleurs et services,
 * et retourne des réponses JSON structurées avec les codes HTTP appropriés.
 *
 * Gère 5 cas d'erreur :
 *   - HTTP 400 : violation de règle métier (BusinessException)
 *   - HTTP 400 : données de formulaire invalides (validation @Valid)
 *   - HTTP 403 : accès refusé (UnauthorizedException, AccessDeniedException)
 *   - HTTP 404 : ressource non trouvée (ResourceNotFoundException)
 *   - HTTP 500 : erreur inattendue (Exception générique)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Structure standard de réponse d'erreur.
     */
    private Map<String, Object> buildErreur(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

    /**
     * Gère les violations de règles métier.
     * HTTP 400 — Bad Request.
     *
     * @param ex exception métier
     * @return réponse d'erreur structurée
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        log.warn("Règle métier violée : {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErreur(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Gère les erreurs de validation des formulaires (@Valid).
     * HTTP 400 — Bad Request avec la liste des champs invalides.
     *
     * @param ex exception de validation Spring MVC
     * @return réponse avec la liste des erreurs par champ
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = buildErreur(HttpStatus.BAD_REQUEST, "Données invalides");
        Map<String, String> erreurs = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("erreurs", erreurs);
        log.warn("Validation échouée : {}", erreurs);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Gère les accès non autorisés.
     * HTTP 403 — Forbidden.
     *
     * @param ex exception d'accès refusé
     * @return réponse d'erreur 403
     */
    @ExceptionHandler({UnauthorizedException.class, AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccesDenied(Exception ex) {
        log.warn("Accès refusé : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(buildErreur(HttpStatus.FORBIDDEN, "Accès refusé : " + ex.getMessage()));
    }

    /**
     * Gère les ressources non trouvées.
     * HTTP 404 — Not Found.
     *
     * @param ex exception de ressource introuvable
     * @return réponse d'erreur 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Ressource non trouvée : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildErreur(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Gère toutes les autres exceptions non prévues.
     * HTTP 500 — Internal Server Error.
     *
     * @param ex exception inattendue
     * @return réponse d'erreur 500 générique
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Erreur inattendue : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErreur(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur inattendue s'est produite. Veuillez contacter l'administrateur."));
    }
}
