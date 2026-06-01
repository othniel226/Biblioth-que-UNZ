package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.TypeNotification;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests pour Notification — couvre les 69 instructions manquées
 */
@DisplayName("Notification — Tests du modèle")
class NotificationModelTest {

    @Test
    @DisplayName("✅ lu = false par défaut")
    void notification_luFalseParDefaut() {
        Notification n = new Notification();
        assertThat(n.getLu()).isFalse();
    }

    @Test
    @DisplayName("✅ dateEnvoi initialisée à la construction")
    void notification_dateEnvoiInitialisee() {
        Notification n = new Notification();
        assertThat(n.getDateEnvoi()).isNotNull();
        assertThat(n.getDateEnvoi()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("✅ marquerLu() → lu passe à true")
    void marquerLu_luDevientTrue() {
        Notification n = new Notification();
        n.marquerLu();
        assertThat(n.getLu()).isTrue();
    }

    @Test
    @DisplayName("✅ sujet et message peuvent être définis")
    void notification_sujetEtMessage() {
        Notification n = new Notification();
        n.setSujet("Rappel de retour");
        n.setMessage("Votre ouvrage doit être rendu dans 3 jours.");
        assertThat(n.getSujet()).isEqualTo("Rappel de retour");
        assertThat(n.getMessage()).contains("3 jours");
    }

    @Test
    @DisplayName("✅ type peut être défini")
    void notification_type() {
        Notification n = new Notification();
        n.setType(TypeNotification.RAPPEL_RETOUR);
        assertThat(n.getType()).isEqualTo(TypeNotification.RAPPEL_RETOUR);
    }

    @Test
    @DisplayName("✅ destinataire peut être défini")
    void notification_destinataire() {
        Etudiant etudiant = new Etudiant();
        etudiant.setEmail("moussa@etud.unz.bf");

        Notification n = new Notification();
        n.setDestinataire(etudiant);
        assertThat(n.getDestinataire()).isEqualTo(etudiant);
    }

    @Test
    @DisplayName("✅ type CONFIRMATION_EMPRUNT")
    void notification_typeConfirmationEmprunt() {
        Notification n = new Notification();
        n.setType(TypeNotification.CONFIRMATION_EMPRUNT);
        assertThat(n.getType()).isEqualTo(TypeNotification.CONFIRMATION_EMPRUNT);
    }

    @Test
    @DisplayName("✅ type ALERTE_RETARD")
    void notification_typeAlerteRetard() {
        Notification n = new Notification();
        n.setType(TypeNotification.ALERTE_RETARD);
        assertThat(n.getType()).isEqualTo(TypeNotification.ALERTE_RETARD);
    }

    @Test
    @DisplayName("✅ type RESERVATION_DISPO")
    void notification_typeReservationDispo() {
        Notification n = new Notification();
        n.setType(TypeNotification.RESERVATION_DISPO);
        assertThat(n.getType()).isEqualTo(TypeNotification.RESERVATION_DISPO);
    }

    @Test
    @DisplayName("✅ dateEnvoi peut être modifiée")
    void notification_dateEnvoiModifiable() {
        Notification n = new Notification();
        LocalDateTime date = LocalDateTime.of(2026, 5, 31, 10, 0);
        n.setDateEnvoi(date);
        assertThat(n.getDateEnvoi()).isEqualTo(date);
    }
}
