package com.unz.bibliotheque.model.enums;

/**
 * État physique d'un exemplaire d'ouvrage.
 *
 * DISPONIBLE → peut être emprunté immédiatement
 * EMPRUNTE   → actuellement entre les mains d'un étudiant
 * RESERVE    → mis de côté pour une réservation confirmée (48h)
 * PERDU      → signalé perdu, retiré de la circulation
 * DETERIORE  → endommagé, en attente de réparation ou remplacement
 */
public enum StatutExemplaire {
    DISPONIBLE,
    EMPRUNTE,
    RESERVE,
    PERDU,
    DETERIORE
}
