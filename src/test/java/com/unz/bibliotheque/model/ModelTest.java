package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutEmprunt;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests des entités JPA — basés sur le code source réel
 */
@DisplayName("Entités JPA — Tests du modèle")
class ModelTest {

    // ══════════════════════════════════════════════════════
    // EMPRUNT
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Emprunt — estEnRetard() et getNombreJoursRetard()")
    class EmpruntTest {

        @Test
        @DisplayName("✅ estEnRetard() = true si date dépassée et statut EN_COURS")
        void estEnRetard_retourneTrue_quandDateDepassee() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().minusDays(3));

            assertThat(e.estEnRetard()).isTrue();
        }

        @Test
        @DisplayName("✅ estEnRetard() = false si date non dépassée")
        void estEnRetard_retourneFalse_quandDateFuture() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().plusDays(5));

            assertThat(e.estEnRetard()).isFalse();
        }

        @Test
        @DisplayName("✅ estEnRetard() = false si statut RENDU (même si date dépassée)")
        void estEnRetard_retourneFalse_quandRendu() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.RENDU);
            e.setDateRetourPrevu(LocalDate.now().minusDays(10));

            assertThat(e.estEnRetard()).isFalse();
        }

        @Test
        @DisplayName("✅ estEnRetard() = false si dateRetourPrevu est null")
        void estEnRetard_retourneFalse_quandDateNull() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(null);

            assertThat(e.estEnRetard()).isFalse();
        }

        @Test
        @DisplayName("✅ getNombreJoursRetard() = 3 pour 3 jours de retard")
        void getNombreJoursRetard_retourne3() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().minusDays(3));

            assertThat(e.getNombreJoursRetard()).isEqualTo(3L);
        }

        @Test
        @DisplayName("✅ getNombreJoursRetard() = 0 si pas en retard")
        void getNombreJoursRetard_retourneZero_sansRetard() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().plusDays(5));

            assertThat(e.getNombreJoursRetard()).isEqualTo(0L);
        }

        @Test
        @DisplayName("✅ clore() — statut passe à RENDU, dateRetourReel enregistrée")
        void clore_metsAJourStatutEtDate() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().plusDays(3));

            LocalDate aujourd = LocalDate.now();
            e.clore(aujourd);

            assertThat(e.getStatut()).isEqualTo(StatutEmprunt.RENDU);
            assertThat(e.getDateRetourReel()).isEqualTo(aujourd);
        }

        @Test
        @DisplayName("✅ clore() — emprunt en retard devient RENDU")
        void clore_empruntEnRetard_devientRendu() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().minusDays(5));

            e.clore(LocalDate.now());

            assertThat(e.getStatut()).isEqualTo(StatutEmprunt.RENDU);
            assertThat(e.estEnRetard()).isFalse(); // RENDU → plus en retard
        }

        @Test
        @DisplayName("✅ prolonge = false par défaut à la construction")
        void prolonge_falseParDefaut() {
            Emprunt e = new Emprunt();
            assertThat(e.getProlonge()).isFalse();
        }

        @Test
        @DisplayName("✅ statut = EN_COURS par défaut à la construction")
        void statut_EN_COURS_parDefaut() {
            Emprunt e = new Emprunt();
            assertThat(e.getStatut()).isEqualTo(StatutEmprunt.EN_COURS);
        }

        @Test
        @DisplayName("✅ getNombreJoursRetard() utilise dateRetourReel si disponible")
        void getNombreJoursRetard_utiliseDateRetourReel() {
            Emprunt e = new Emprunt();
            e.setStatut(StatutEmprunt.EN_COURS);
            e.setDateRetourPrevu(LocalDate.now().minusDays(5));
            e.setDateRetourReel(LocalDate.now().minusDays(2)); // rendu il y a 2 jours

            assertThat(e.getNombreJoursRetard()).isEqualTo(3L); // 5 - 2 = 3 jours
        }
    }

    // ══════════════════════════════════════════════════════
    // PENALITE
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Penalite — marquerPayee() et valeurs par défaut")
    class PenaliteTest {

        @Test
        @DisplayName("✅ payee = false par défaut")
        void payee_falseParDefaut() {
            Penalite p = new Penalite();
            assertThat(p.getPayee()).isFalse();
        }

        @Test
        @DisplayName("✅ montant = ZERO par défaut")
        void montant_zeroParDefaut() {
            Penalite p = new Penalite();
            assertThat(p.getMontant()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("✅ joursRetard = 0 par défaut")
        void joursRetard_zeroParDefaut() {
            Penalite p = new Penalite();
            assertThat(p.getJoursRetard()).isEqualTo(0L);
        }

        @Test
        @DisplayName("✅ marquerPayee() — payee passe à true")
        void marquerPayee_payeeDevientTrue() {
            Penalite p = new Penalite();
            p.marquerPayee();
            assertThat(p.getPayee()).isTrue();
        }

        @Test
        @DisplayName("✅ marquerPayee() — datePaiement enregistrée")
        void marquerPayee_datePaiementEnregistree() {
            Penalite p = new Penalite();
            p.marquerPayee();
            assertThat(p.getDatePaiement()).isNotNull();
            assertThat(p.getDatePaiement()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("✅ Pénalité avec montant 500 FCFA et 5 jours")
        void penalite_montantEtJoursCorrects() {
            Penalite p = new Penalite();
            p.setMontant(BigDecimal.valueOf(500));
            p.setJoursRetard(5L);

            assertThat(p.getMontant()).isEqualByComparingTo(BigDecimal.valueOf(500));
            assertThat(p.getJoursRetard()).isEqualTo(5L);
        }

        @Test
        @DisplayName("✅ strategieUtilisee peut être enregistrée")
        void strategieUtilisee_enregistree() {
            Penalite p = new Penalite();
            p.setStrategieUtilisee("Tarif Fixe 100 FCFA/jour");
            assertThat(p.getStrategieUtilisee()).isEqualTo("Tarif Fixe 100 FCFA/jour");
        }
    }

    // ══════════════════════════════════════════════════════
    // STATUT EMPRUNT (enum)
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("StatutEmprunt — valeurs de l'enum")
    class StatutEmpruntTest {

        @Test
        @DisplayName("✅ Les statuts EN_COURS, PROLONGE, EN_RETARD, RENDU existent")
        void statutEmprunt_valeursExistent() {
            assertThat(StatutEmprunt.values()).contains(
                StatutEmprunt.EN_COURS,
                StatutEmprunt.RENDU
            );
        }

        @Test
        @DisplayName("✅ valueOf() fonctionne pour EN_COURS")
        void statutEmprunt_valueOf_EN_COURS() {
            assertThat(StatutEmprunt.valueOf("EN_COURS")).isEqualTo(StatutEmprunt.EN_COURS);
        }

        @Test
        @DisplayName("✅ valueOf() fonctionne pour RENDU")
        void statutEmprunt_valueOf_RENDU() {
            assertThat(StatutEmprunt.valueOf("RENDU")).isEqualTo(StatutEmprunt.RENDU);
        }
    }
}
