package com.unz.bibliotheque.pattern.strategy;

import com.unz.bibliotheque.model.Emprunt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Implémentation du Pattern Strategy : tarif fixe par jour de retard.
 *
 * Calcul : montant = nombre_jours_retard × tarif_par_jour_FCFA
 * Exemple : 5 jours × 100 FCFA = 500 FCFA
 *
 * @Primary : Spring utilisera cette stratégie par défaut quand plusieurs
 * beans PenaliteStrategy sont disponibles.
 *
 * Le tarif est configurable via application.yml ou ConfigurationSysteme.
 */
@Component("tarifFixe")
@Primary
public class TarifFixeStrategy implements PenaliteStrategy {

    /**
     * Tarif par jour de retard en FCFA.
     * Valeur par défaut : 100 FCFA/jour (configurable dans application.yml).
     */
    @Value("${app.bibliotheque.penalite-par-jour-fcfa:100}")
    private int tarifParJour;

    /**
     * {@inheritDoc}
     *
     * Calcule la pénalité avec un tarif fixe par jour.
     * Retourne ZERO si pas de retard.
     */
    @Override
    public BigDecimal calculer(Emprunt emprunt) {
        long joursRetard = emprunt.getNombreJoursRetard();
        if (joursRetard <= 0) {
            return BigDecimal.ZERO;
        }
        // Calcul : jours_retard × tarif_par_jour
        return BigDecimal.valueOf(joursRetard * tarifParJour);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Tarif fixe — " + tarifParJour + " FCFA par jour de retard";
    }
}
