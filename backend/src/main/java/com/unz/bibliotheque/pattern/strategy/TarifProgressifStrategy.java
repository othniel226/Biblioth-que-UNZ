package com.unz.bibliotheque.pattern.strategy;

import com.unz.bibliotheque.model.Emprunt;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Implémentation du Pattern Strategy : tarif progressif (croissant).
 *
 * Le tarif augmente par palier selon le nombre de jours de retard :
 *   - Jours  1 à  7 : 100 FCFA/jour
 *   - Jours  8 à 14 : 150 FCFA/jour (×1.5)
 *   - Jours 15+     : 200 FCFA/jour (×2.0)
 *
 * Cette stratégie encourage les étudiants à rendre rapidement les ouvrages.
 * Elle peut être activée par l'administrateur via ConfigurationSysteme.
 */
@Component("tarifProgressif")
public class TarifProgressifStrategy implements PenaliteStrategy {

    /** Tarif de base pour les 7 premiers jours */
    private static final int TARIF_BASE = 100;

    /** Tarif intermédiaire (jours 8 à 14) */
    private static final int TARIF_INTERMEDIAIRE = 150;

    /** Tarif maximum (au-delà de 14 jours) */
    private static final int TARIF_MAXIMUM = 200;

    /**
     * {@inheritDoc}
     *
     * Calcule la pénalité avec un tarif progressif par palier.
     */
    @Override
    public BigDecimal calculer(Emprunt emprunt) {
        long jours = emprunt.getNombreJoursRetard();
        if (jours <= 0) {
            return BigDecimal.ZERO;
        }

        long montant = 0;

        // Palier 1 : jours 1 à 7
        long palier1 = Math.min(jours, 7);
        montant += palier1 * TARIF_BASE;

        // Palier 2 : jours 8 à 14
        if (jours > 7) {
            long palier2 = Math.min(jours - 7, 7);
            montant += palier2 * TARIF_INTERMEDIAIRE;
        }

        // Palier 3 : jours 15 et plus
        if (jours > 14) {
            long palier3 = jours - 14;
            montant += palier3 * TARIF_MAXIMUM;
        }

        return BigDecimal.valueOf(montant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Tarif progressif — 100/150/200 FCFA/jour selon les paliers";
    }
}
