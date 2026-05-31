package com.unz.bibliotheque.model.enums;

/**
 * Cycle de vie d'un emprunt.
 *
 * EN_COURS   → l'exemplaire est entre les mains de l'étudiant
 * EN_RETARD  → date de retour prévue dépassée, pénalité calculée
 * PROLONGE   → durée étendue une fois (une seule prolongation autorisée)
 * RENDU      → retour enregistré par le bibliothécaire
 */
public enum StatutEmprunt {
    /** Emprunt actif, dans les délais */
    EN_COURS,
    /** Emprunt actif, date de retour dépassée */
    EN_RETARD,
    /** Emprunt prolongé (une seule fois autorisé) */
    PROLONGE,
    /** Livre retourné — emprunt clôturé */
    RENDU
}
