package com.unz.bibliotheque.model.enums;

/**
 * Cycle de vie d'une réservation.
 *
 * EN_ATTENTE  → dans la file FIFO, pas encore de disponibilité
 * CONFIRMEE   → un exemplaire est réservé (48h pour venir le chercher)
 * EXPIREE     → délai de 48h dépassé sans récupération
 * ANNULEE     → annulée par l'étudiant
 * TRANSFORMEE → convertie en emprunt
 */
public enum StatutReservation {
    EN_ATTENTE,
    CONFIRMEE,
    EXPIREE,
    ANNULEE,
    TRANSFORMEE
}
