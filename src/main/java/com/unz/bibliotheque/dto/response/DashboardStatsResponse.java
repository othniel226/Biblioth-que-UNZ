package com.unz.bibliotheque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de réponse pour les statistiques du tableau de bord.
 * Utilisé par les contrôleurs Admin et Bibliothécaire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    // Utilisateurs
    private Long nbUtilisateurs;
    private Long nbEtudiants;
    private Long nbBibliothecaires;
    private Long nbAdministrateurs;

    // Emprunts
    private Long nbEmpruntsTotal;
    private Long nbEmpruntsEnCours;
    private Long nbEmpruntsEnRetard;
    private Long nbEmpruntsRendus;

    // Catalogue
    private Long nbOuvrages;
    private Long nbExemplaires;
    private Long nbExemplairesDisponibles;

    // Réservations & Pénalités
    private Long nbReservationsEnAttente;
    private Long nbPenalitesImpayees;
    private BigDecimal montantPenalitesImpayees;

    // Notifications
    private Long nbNotificationsNonLues;
}
