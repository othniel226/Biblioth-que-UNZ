package com.unz.bibliotheque.scheduler;

import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.repository.EmpruntRepository;
import com.unz.bibliotheque.service.ConfigurationService;
import com.unz.bibliotheque.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

/**
 * Tâche planifiée : envoi des rappels de retour J-3.
 *
 * S'exécute chaque jour à 8h00 (heure du serveur).
 * Envoie un email de rappel aux étudiants dont le retour est prévu
 * dans X jours (configurable via ConfigurationSysteme.JOURS_AVANT_RAPPEL).
 *
 * Expression CRON : "0 0 8 * * *"
 *   - 0 secondes, 0 minutes, 8 heures, tous les jours
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RappelRetourScheduler {

    private final EmpruntRepository    empruntRepo;
    private final NotificationService  notificationService;
    private final ConfigurationService configService;

    /**
     * Envoie les rappels de retour à 8h00 chaque jour.
     *
     * Algorithme :
     * 1. Calculer la date cible (aujourd'hui + X jours)
     * 2. Trouver tous les emprunts dont la date de retour = date cible
     * 3. Envoyer un email de rappel pour chacun
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void envoyerRappels() {
        int joursAvant = configService.getJoursAvantRappel();
        LocalDate dateCible = LocalDate.now().plusDays(joursAvant);

        log.info("Scheduler rappel retour — date cible : {} (J-{})", dateCible, joursAvant);

        List<Emprunt> empruntsACibler = empruntRepo.findByDateRetourPrevuAndStatutIn(dateCible);

        log.info("{} emprunt(s) à rappeler", empruntsACibler.size());

        for (Emprunt emprunt : empruntsACibler) {
            try {
                notificationService.envoyerRappelRetour(emprunt);
            } catch (Exception e) {
                log.error("Erreur rappel pour emprunt {} : {}", emprunt.getId(), e.getMessage());
            }
        }

        log.info("Rappels envoyés : {}/{}", empruntsACibler.size(), empruntsACibler.size());
    }
}
