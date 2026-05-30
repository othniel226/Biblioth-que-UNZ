package com.unz.bibliotheque.service;

import com.unz.bibliotheque.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion de la configuration du système.
 *
 * Permet de lire les paramètres métier configurables par l'administrateur
 * (durée d'emprunt, quotas, pénalités, etc.) depuis la table
 * configurations_systeme.
 *
 * Fournit des valeurs par défaut si la clé n'est pas trouvée en base.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ConfigurationRepository configRepo;

    /**
     * Retourne la valeur d'une configuration sous forme d'entier.
     * Si la clé n'existe pas, retourne la valeur par défaut fournie.
     *
     * @param cle          clé de configuration (ex: DUREE_EMPRUNT_JOURS)
     * @param valeurDefaut valeur par défaut si la clé est absente
     * @return valeur entière de la configuration
     */
    public int getInt(String cle, int valeurDefaut) {
        return configRepo.findByCle(cle)
            .map(c -> {
                try {
                    return c.getValeurAsInt();
                } catch (NumberFormatException e) {
                    log.warn("Configuration '{}' n'est pas un entier valide : {}", cle, c.getValeur());
                    return valeurDefaut;
                }
            })
            .orElse(valeurDefaut);
    }

    /**
     * Retourne la valeur d'une configuration sous forme de chaîne.
     * Si la clé n'existe pas, retourne la valeur par défaut fournie.
     *
     * @param cle          clé de configuration
     * @param valeurDefaut valeur par défaut si la clé est absente
     * @return valeur de la configuration en String
     */
    public String getString(String cle, String valeurDefaut) {
        return configRepo.findByCle(cle)
            .map(c -> c.getValeur())
            .orElse(valeurDefaut);
    }

    // ── Raccourcis pour les paramètres les plus utilisés ──

    /** @return durée d'emprunt en jours (défaut : 14 jours) */
    public int getDureeEmpruntJours()          { return getInt("DUREE_EMPRUNT_JOURS", 14); }

    /** @return quota maximum d'emprunts simultanés (défaut : 3) */
    public int getMaxEmpruntsSimultanes()      { return getInt("MAX_EMPRUNTS_SIMULTANES", 3); }

    /** @return quota maximum de réservations (défaut : 5) */
    public int getMaxReservationsSimultanes()  { return getInt("MAX_RESERVATIONS_SIMULTANES", 5); }

    /** @return pénalité par jour de retard en FCFA (défaut : 100 FCFA) */
    public int getPenaliteParJour()            { return getInt("PENALITE_PAR_JOUR_FCFA", 100); }

    /** @return seuil de blocage du compte en FCFA (défaut : 500 FCFA) */
    public int getSeuilBlocageFcfa()           { return getInt("SEUIL_BLOCAGE_FCFA", 500); }

    /** @return délai de réservation confirmée en heures (défaut : 48h) */
    public int getDureeReservationHeures()     { return getInt("DUREE_RESERVATION_HEURES", 48); }

    /** @return nombre de jours avant rappel de retour (défaut : 3 jours) */
    public int getJoursAvantRappel()           { return getInt("JOURS_AVANT_RAPPEL", 3); }
}
