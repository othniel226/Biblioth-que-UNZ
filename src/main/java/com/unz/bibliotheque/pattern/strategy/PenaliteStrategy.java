package com.unz.bibliotheque.pattern.strategy;

import com.unz.bibliotheque.model.Emprunt;
import java.math.BigDecimal;

/**
 * Interface du Pattern Strategy pour le calcul des pénalités de retard.
 *
 * Design Pattern : Strategy
 * Objectif : permettre de changer l'algorithme de calcul des pénalités
 * sans modifier le code de EmpruntService.
 *
 * Deux implémentations disponibles :
 *   - {@link TarifFixeStrategy}       : montant fixe par jour (ex: 100 FCFA/jour)
 *   - {@link TarifProgressifStrategy} : tarif augmentant avec les jours
 *
 * L'administrateur peut choisir la stratégie via ConfigurationSysteme.
 */
public interface PenaliteStrategy {

    /**
     * Calcule le montant de la pénalité pour un emprunt en retard.
     *
     * @param emprunt l'emprunt pour lequel calculer la pénalité
     * @return montant de la pénalité en FCFA (BigDecimal pour précision financière)
     */
    BigDecimal calculer(Emprunt emprunt);

    /**
     * Retourne une description lisible de la stratégie utilisée.
     * Utilisée pour la traçabilité dans l'entité Penalite.
     *
     * @return description de la stratégie
     */
    String getDescription();
}
