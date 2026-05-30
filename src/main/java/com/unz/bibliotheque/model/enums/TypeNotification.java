package com.unz.bibliotheque.model.enums;

/**
 * Types de notifications envoyées par email aux utilisateurs.
 *
 * RAPPEL_RETOUR        → rappel J-3 avant la date de retour
 * ALERTE_RETARD        → alerte quand la date de retour est dépassée
 * RESERVATION_DISPO    → un ouvrage réservé est maintenant disponible
 * CONFIRMATION_EMPRUNT → confirmation de création d'emprunt
 * PROLONGATION_ACCORD  → prolongation accordée
 * PENALITE_ENCAISSEE   → pénalité payée avec succès
 * COMPTE_CREE          → email de bienvenue à la création du compte
 */
public enum TypeNotification {
    RAPPEL_RETOUR,
    ALERTE_RETARD,
    RESERVATION_DISPO,
    CONFIRMATION_EMPRUNT,
    PROLONGATION_ACCORD,
    PENALITE_ENCAISSEE,
    COMPTE_CREE
}
