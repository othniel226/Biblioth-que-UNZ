package com.unz.bibliotheque.model.enums;

/**
 * Énumération des rôles utilisateurs dans le système.
 * Utilisée par Spring Security pour le contrôle d'accès (RBAC).
 *
 * - ETUDIANT    : peut emprunter, réserver, consulter son historique
 * - BIBLIOTHECAIRE : peut gérer le catalogue, enregistrer emprunts/retours
 * - ADMINISTRATEUR : accès total, gestion utilisateurs, rapports, configuration
 */
public enum Role {
    /** Étudiant de l'université */
    ETUDIANT,
    /** Bibliothécaire de la bibliothèque universitaire */
    BIBLIOTHECAIRE,
    /** Administrateur système */
    ADMINISTRATEUR
}
