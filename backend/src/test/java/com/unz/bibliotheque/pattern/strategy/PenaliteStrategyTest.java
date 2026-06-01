package com.unz.bibliotheque.pattern.strategy;

import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests pour le Strategy Pattern — PenaliteStrategy
 * Basés sur le code source réel de TarifFixeStrategy
 * Méthode réelle : calculer(Emprunt) retourne BigDecimal
 */
@DisplayName("Strategy Pattern — Calcul des pénalités")
class PenaliteStrategyTest {

    // ══════════════════════════════════════════════════════
    // TARIF FIXE : 100 FCFA / JOUR
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("TarifFixeStrategy — 100 FCFA par jour")
    class TarifFixeStrategyTests {

        private TarifFixeStrategy strategy;

        @BeforeEach
        void setUp() {
            strategy = new TarifFixeStrategy();
            // Injecter le tarif via ReflectionTestUtils (remplace @Value)
            ReflectionTestUtils.setField(strategy, "tarifParJour", 100);
        }

        @Test
        @DisplayName("✅ 0 jour de retard → ZERO FCFA")
        void calculer_zeroJourRetard_retourneZero() {
            Emprunt emprunt = empruntAvecRetard(0);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("✅ 1 jour de retard → 100 FCFA")
        void calculer_unJour_retourneCent() {
            Emprunt emprunt = empruntAvecRetard(1);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("✅ 5 jours de retard → 500 FCFA (seuil de blocage)")
        void calculer_cinqJours_retourneCinqCents() {
            Emprunt emprunt = empruntAvecRetard(5);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.valueOf(500));
        }

        @Test
        @DisplayName("✅ 10 jours de retard → 1000 FCFA")
        void calculer_dixJours_retourneMille() {
            Emprunt emprunt = empruntAvecRetard(10);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("✅ 14 jours de retard → 1400 FCFA (durée d'un emprunt)")
        void calculer_14Jours_retourne1400() {
            Emprunt emprunt = empruntAvecRetard(14);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.valueOf(1400));
        }

        @ParameterizedTest(name = "{0} jours → {1} FCFA")
        @CsvSource({
                "1,  100",
                "3,  300",
                "7,  700",
                "14, 1400",
                "30, 3000"
        })
        @DisplayName("✅ Calcul linéaire : N jours × 100 FCFA")
        void calculer_proportionnel(int jours, long montantAttendu) {
            Emprunt emprunt = empruntAvecRetard(jours);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.valueOf(montantAttendu));
        }

        @Test
        @DisplayName("✅ getDescription() contient le tarif")
        void getDescription_contientTarif() {
            assertThat(strategy.getDescription())
                    .contains("100")
                    .contains("FCFA");
        }

        @Test
        @DisplayName("✅ Emprunt non en retard → retourne ZERO (pas de pénalité)")
        void calculer_pasEnRetard_retourneZero() {
            Emprunt emprunt = new Emprunt();
            emprunt.setStatut(StatutEmprunt.EN_COURS);
            emprunt.setDateRetourPrevu(LocalDate.now().plusDays(5)); // futur
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("✅ Emprunt RENDU → retourne ZERO")
        void calculer_empruntRendu_retourneZero() {
            Emprunt emprunt = new Emprunt();
            emprunt.setStatut(StatutEmprunt.RENDU);
            emprunt.setDateRetourPrevu(LocalDate.now().minusDays(5));
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // ══════════════════════════════════════════════════════
    // TARIF PROGRESSIF
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("TarifProgressifStrategy — paliers croissants")
    class TarifProgressifStrategyTests {

        private TarifProgressifStrategy strategy;

        @BeforeEach
        void setUp() {
            strategy = new TarifProgressifStrategy();
            // Injecter les valeurs par défaut via ReflectionTestUtils si nécessaire
            try {
                ReflectionTestUtils.setField(strategy, "tarifParJour", 100);
            } catch (Exception ignored) {}
        }

        @Test
        @DisplayName("✅ 0 jour → ZERO FCFA")
        void calculer_zeroJour_retourneZero() {
            Emprunt emprunt = empruntAvecRetard(0);
            assertThat(strategy.calculer(emprunt))
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("✅ Retard positif → montant positif")
        void calculer_retardPositif_montantPositif() {
            Emprunt emprunt = empruntAvecRetard(3);
            assertThat(strategy.calculer(emprunt))
                    .isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("✅ Plus de jours → montant plus élevé (croissance)")
        void calculer_montantCroissantAvecJours() {
            BigDecimal p3  = strategy.calculer(empruntAvecRetard(3));
            BigDecimal p7  = strategy.calculer(empruntAvecRetard(7));
            BigDecimal p14 = strategy.calculer(empruntAvecRetard(14));

            assertThat(p3).isLessThanOrEqualTo(p7);
            assertThat(p7).isLessThanOrEqualTo(p14);
        }

        @Test
        @DisplayName("✅ getDescription() retourne une description non vide")
        void getDescription_retourneDescriptionNonVide() {
            assertThat(strategy.getDescription()).isNotBlank();
        }

        @ParameterizedTest(name = "Retard {0} jours → montant ≥ 0")
        @ValueSource(ints = {1, 2, 5, 10, 20, 30})
        @DisplayName("✅ Montant toujours ≥ 0 pour tout retard")
        void calculer_montantToujours_nonNegatif(int jours) {
            assertThat(strategy.calculer(empruntAvecRetard(jours)))
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    // ══════════════════════════════════════════════════════
    // COMPARAISON DES DEUX STRATÉGIES
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Polymorphisme — les deux stratégies respectent le contrat")
    class PolymorphismeTest {

        @Test
        @DisplayName("✅ Les deux implémentent PenaliteStrategy")
        void deuxStrategies_implemententInterface() {
            TarifFixeStrategy fixe = new TarifFixeStrategy();
            TarifProgressifStrategy progressif = new TarifProgressifStrategy();

            assertThat(fixe).isInstanceOf(PenaliteStrategy.class);
            assertThat(progressif).isInstanceOf(PenaliteStrategy.class);
        }

        @Test
        @DisplayName("✅ Les deux retournent ZERO pour 0 jour de retard")
        void deuxStrategies_zeroJour_retournentZero() {
            TarifFixeStrategy fixe = new TarifFixeStrategy();
            ReflectionTestUtils.setField(fixe, "tarifParJour", 100);
            TarifProgressifStrategy progressif = new TarifProgressifStrategy();

            Emprunt emprunt = empruntAvecRetard(0);

            assertThat(fixe.calculer(emprunt)).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(progressif.calculer(emprunt)).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("✅ Les deux ont une description non nulle")
        void deuxStrategies_descriptionNonNulle() {
            TarifFixeStrategy fixe = new TarifFixeStrategy();
            ReflectionTestUtils.setField(fixe, "tarifParJour", 100);
            TarifProgressifStrategy progressif = new TarifProgressifStrategy();

            assertThat(fixe.getDescription()).isNotNull();
            assertThat(progressif.getDescription()).isNotNull();
        }
    }

    // ══════════════════════════════════════════════════════
    // HELPER
    // ══════════════════════════════════════════════════════

    /**
     * Crée un emprunt avec N jours de retard.
     * Si N=0 → date retour dans le futur (pas de retard).
     */
    private Emprunt empruntAvecRetard(int joursRetard) {
        Emprunt emprunt = new Emprunt();
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        if (joursRetard <= 0) {
            emprunt.setDateRetourPrevu(LocalDate.now().plusDays(1));
        } else {
            emprunt.setDateRetourPrevu(LocalDate.now().minusDays(joursRetard));
        }
        return emprunt;
    }
}
