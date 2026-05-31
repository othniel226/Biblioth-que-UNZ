package com.unz.bibliotheque.scheduler;

import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.repository.EmpruntRepository;
import com.unz.bibliotheque.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Tâche planifiée : détection et traitement des emprunts en retard.
 *
 * S'exécute chaque jour à 9h00 (après le rappel de 8h00).
 * Met à jour le statut des emprunts dépassés à EN_RETARD et
 * envoie des alertes aux étudiants concernés.
 *
 * Expression CRON : "0 0 9 * * *"
 *   - 0 secondes, 0 minutes, 9 heures, tous les jours
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationRetardScheduler {

    private final EmpruntRepository   empruntRepo;
    private final NotificationService notificationService;

    /**
     * Détecte les emprunts en retard et envoie les alertes à 9h00.
     *
     * Algorithme :
     * 1. Trouver tous les emprunts dont la date de retour est dépassée
     * 2. Mettre à jour leur statut à EN_RETARD
     * 3. Envoyer une alerte email à chaque étudiant concerné
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void verifierRetards() {
        LocalDate aujourd_hui = LocalDate.now();
        log.info("Scheduler vérification retards — date : {}", aujourd_hui);

        List<Emprunt> empruntsEnRetard = empruntRepo.findEmpruntsEnRetard(aujourd_hui);

        log.info("{} emprunt(s) en retard détecté(s)", empruntsEnRetard.size());

        int alertesEnvoyees = 0;
        for (Emprunt emprunt : empruntsEnRetard) {
            try {
                // Mettre à jour le statut si pas encore EN_RETARD
                if (emprunt.getStatut() != StatutEmprunt.EN_RETARD) {
                    emprunt.setStatut(StatutEmprunt.EN_RETARD);
                    empruntRepo.save(emprunt);
                }

                // Envoyer l'alerte email
                notificationService.envoyerAlerteRetard(emprunt);
                alertesEnvoyees++;

            } catch (Exception e) {
                log.error("Erreur traitement retard emprunt {} : {}", emprunt.getId(), e.getMessage());
            }
        }

        log.info("Vérification retards terminée : {}/{} alertes envoyées",
            alertesEnvoyees, empruntsEnRetard.size());
    }
}
