package com.unz.bibliotheque.service;

import com.unz.bibliotheque.model.ConfigurationSysteme;
import com.unz.bibliotheque.repository.ConfigurationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationService — Paramètres système")
class ConfigurationServiceTest {

    @Mock
    private ConfigurationRepository configRepo;

    @InjectMocks
    private ConfigurationService configService;

    // ── getInt ────────────────────────────────────────────────

    @Test
    @DisplayName("✅ getInt — clé existante retourne la valeur en base")
    void getInt_cleExistante_retourneValeurBase() {
        ConfigurationSysteme config = mock(ConfigurationSysteme.class);
        when(config.getValeurAsInt()).thenReturn(21);
        when(configRepo.findByCle("DUREE_EMPRUNT_JOURS")).thenReturn(Optional.of(config));

        int result = configService.getInt("DUREE_EMPRUNT_JOURS", 14);
        assertThat(result).isEqualTo(21);
    }

    @Test
    @DisplayName("✅ getInt — clé absente retourne la valeur par défaut")
    void getInt_cleAbsente_retourneDefaut() {
        when(configRepo.findByCle("CLE_INEXISTANTE")).thenReturn(Optional.empty());

        int result = configService.getInt("CLE_INEXISTANTE", 99);
        assertThat(result).isEqualTo(99);
    }

    @Test
    @DisplayName("✅ getInt — valeur non entière retourne le défaut (robustesse)")
    void getInt_valeurNonEntiere_retourneDefaut() {
        ConfigurationSysteme config = mock(ConfigurationSysteme.class);
        when(config.getValeurAsInt()).thenThrow(new NumberFormatException("abc"));
        when(configRepo.findByCle("CLE_INVALIDE")).thenReturn(Optional.of(config));

        int result = configService.getInt("CLE_INVALIDE", 42);
        assertThat(result).isEqualTo(42);
    }

    // ── getString ─────────────────────────────────────────────

    @Test
    @DisplayName("✅ getString — clé existante retourne la valeur")
    void getString_cleExistante_retourneValeur() {
        ConfigurationSysteme config = mock(ConfigurationSysteme.class);
        when(config.getValeur()).thenReturn("PROGRESSIF");
        when(configRepo.findByCle("STRATEGIE_PENALITE")).thenReturn(Optional.of(config));

        String result = configService.getString("STRATEGIE_PENALITE", "FIXE");
        assertThat(result).isEqualTo("PROGRESSIF");
    }

    @Test
    @DisplayName("✅ getString — clé absente retourne le défaut")
    void getString_cleAbsente_retourneDefaut() {
        when(configRepo.findByCle("CLE_ABSENTE")).thenReturn(Optional.empty());

        String result = configService.getString("CLE_ABSENTE", "valeur_defaut");
        assertThat(result).isEqualTo("valeur_defaut");
    }

    // ── Raccourcis métier ─────────────────────────────────────

    @Test
    @DisplayName("✅ getDureeEmpruntJours — défaut = 14 jours si clé absente")
    void getDureeEmpruntJours_defaut14() {
        when(configRepo.findByCle("DUREE_EMPRUNT_JOURS")).thenReturn(Optional.empty());
        assertThat(configService.getDureeEmpruntJours()).isEqualTo(14);
    }

    @Test
    @DisplayName("✅ getMaxEmpruntsSimultanes — défaut = 3 si clé absente")
    void getMaxEmpruntsSimultanes_defaut3() {
        when(configRepo.findByCle("MAX_EMPRUNTS_SIMULTANES")).thenReturn(Optional.empty());
        assertThat(configService.getMaxEmpruntsSimultanes()).isEqualTo(3);
    }

    @Test
    @DisplayName("✅ getMaxReservationsSimultanes — défaut = 5 si clé absente")
    void getMaxReservationsSimultanes_defaut5() {
        when(configRepo.findByCle("MAX_RESERVATIONS_SIMULTANES")).thenReturn(Optional.empty());
        assertThat(configService.getMaxReservationsSimultanes()).isEqualTo(5);
    }

    @Test
    @DisplayName("✅ getPenaliteParJour — défaut = 100 FCFA si clé absente")
    void getPenaliteParJour_defaut100() {
        when(configRepo.findByCle("PENALITE_PAR_JOUR_FCFA")).thenReturn(Optional.empty());
        assertThat(configService.getPenaliteParJour()).isEqualTo(100);
    }

    @Test
    @DisplayName("✅ getSeuilBlocageFcfa — défaut = 500 FCFA si clé absente")
    void getSeuilBlocageFcfa_defaut500() {
        when(configRepo.findByCle("SEUIL_BLOCAGE_FCFA")).thenReturn(Optional.empty());
        assertThat(configService.getSeuilBlocageFcfa()).isEqualTo(500);
    }

    @Test
    @DisplayName("✅ getDureeReservationHeures — défaut = 48h si clé absente")
    void getDureeReservationHeures_defaut48() {
        when(configRepo.findByCle("DUREE_RESERVATION_HEURES")).thenReturn(Optional.empty());
        assertThat(configService.getDureeReservationHeures()).isEqualTo(48);
    }

    @Test
    @DisplayName("✅ getJoursAvantRappel — défaut = 3 jours si clé absente")
    void getJoursAvantRappel_defaut3() {
        when(configRepo.findByCle("JOURS_AVANT_RAPPEL")).thenReturn(Optional.empty());
        assertThat(configService.getJoursAvantRappel()).isEqualTo(3);
    }
}
