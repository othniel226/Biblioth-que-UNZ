package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.ConfigurationSysteme;
import com.unz.bibliotheque.repository.ConfigurationRepository;
import com.unz.bibliotheque.service.ConfigurationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du ConfigurationService.
 * Vérifie la lecture correcte des paramètres système.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests ConfigurationService")
class ConfigurationServiceTest {

    @Mock
    private ConfigurationRepository configRepo;

    @InjectMocks
    private ConfigurationService configService;

    private ConfigurationSysteme creerConfig(String cle, String valeur) {
        ConfigurationSysteme c = new ConfigurationSysteme();
        c.setCle(cle);
        c.setValeur(valeur);
        return c;
    }

    @Test
    @DisplayName("✅ getDureeEmpruntJours() → retourne la valeur configurée")
    void getDureeEmpruntJours_ValeurConfiguree() {
        when(configRepo.findByCle("DUREE_EMPRUNT_JOURS"))
            .thenReturn(Optional.of(creerConfig("DUREE_EMPRUNT_JOURS", "14")));
        assertThat(configService.getDureeEmpruntJours()).isEqualTo(14);
    }

    @Test
    @DisplayName("✅ getDureeEmpruntJours() → valeur par défaut si non configurée")
    void getDureeEmpruntJours_ValeurDefaut() {
        when(configRepo.findByCle("DUREE_EMPRUNT_JOURS")).thenReturn(Optional.empty());
        assertThat(configService.getDureeEmpruntJours()).isEqualTo(14);
    }

    @Test
    @DisplayName("✅ getMaxEmpruntsSimultanes() → retourne la valeur configurée")
    void getMaxEmpruntsSimultanes_ValeurConfiguree() {
        when(configRepo.findByCle("MAX_EMPRUNTS_SIMULTANES"))
            .thenReturn(Optional.of(creerConfig("MAX_EMPRUNTS_SIMULTANES", "3")));
        assertThat(configService.getMaxEmpruntsSimultanes()).isEqualTo(3);
    }

    @Test
    @DisplayName("✅ getPenaliteParJour() → retourne la valeur configurée")
    void getPenaliteParJour_ValeurConfiguree() {
        when(configRepo.findByCle("PENALITE_PAR_JOUR_FCFA"))
            .thenReturn(Optional.of(creerConfig("PENALITE_PAR_JOUR_FCFA", "100")));
        assertThat(configService.getPenaliteParJour()).isEqualTo(100);
    }

    @Test
    @DisplayName("✅ getSeuilBlocageFcfa() → retourne la valeur configurée")
    void getSeuilBlocage_ValeurConfiguree() {
        when(configRepo.findByCle("SEUIL_BLOCAGE_FCFA"))
            .thenReturn(Optional.of(creerConfig("SEUIL_BLOCAGE_FCFA", "500")));
        assertThat(configService.getSeuilBlocageFcfa()).isEqualTo(500);
    }

   
}
