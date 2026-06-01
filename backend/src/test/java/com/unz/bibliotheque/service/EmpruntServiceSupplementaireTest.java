package com.unz.bibliotheque.service;

import com.unz.bibliotheque.exception.BusinessException;
import com.unz.bibliotheque.exception.ResourceNotFoundException;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.model.enums.StatutExemplaire;
import com.unz.bibliotheque.pattern.strategy.PenaliteStrategy;
import com.unz.bibliotheque.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EmpruntService — Tests supplémentaires")
class EmpruntServiceSupplementaireTest {

    @Mock private EmpruntRepository     empruntRepo;
    @Mock private EtudiantRepository    etudiantRepo;
    @Mock private ExemplaireRepository  exemplaireRepo;
    @Mock private PenaliteRepository    penaliteRepo;
    @Mock private ConfigurationService  configService;
    @Mock private NotificationService   notificationService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PenaliteStrategy      penaliteStrategy;

    @InjectMocks
    private EmpruntService empruntService;

    private Etudiant etudiant;
    private Ouvrage ouvrage;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Ouedraogo");
        etudiant.setPrenom("Moussa");
        etudiant.setEmail("moussa@etud.unz.bf");
        etudiant.setActif(true);

        ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Clean Code");
    }

    // ── RETOUR AVEC PÉNALITÉ ──────────────────────────────────

    @Test
    @DisplayName("✅ Retour avec retard — emprunt clôturé à RENDU")
    void enregistrerRetour_avecRetard_empruntRendu() {
        // L'emprunt EST en retard AVANT clore() — on vérifie que clore() est appelé
        Exemplaire exemplaireReel = new Exemplaire();
        exemplaireReel.setStatut(StatutExemplaire.EMPRUNTE);
        exemplaireReel.setOuvrage(ouvrage);

        // Utiliser un spy pour vérifier que clore() est appelé
        Emprunt emprunt = spy(new Emprunt());
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setExemplaire(exemplaireReel);
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setDateRetourPrevu(LocalDate.now().minusDays(5));
        emprunt.setDateEmprunt(LocalDateTime.now().minusDays(19));
        emprunt.setProlonge(false);

        // estEnRetard() doit retourner true AVANT clore()
        doReturn(true).doReturn(false).when(emprunt).estEnRetard();
        doReturn(5L).when(emprunt).getNombreJoursRetard();

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));
        when(empruntRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(exemplaireRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(penaliteStrategy.calculer(any())).thenReturn(BigDecimal.valueOf(500));
        when(penaliteStrategy.getDescription()).thenReturn("Tarif fixe");
        when(penaliteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publishEvent(any());

        Emprunt result = empruntService.enregistrerRetour(1L);

        assertThat(result.getStatut()).isEqualTo(StatutEmprunt.RENDU);
        verify(penaliteRepo).save(any(Penalite.class));
    }

    @Test
    @DisplayName("✅ Retour sans retard — statut RENDU, pas de pénalité")
    void enregistrerRetour_sansRetard_pasDePenalite() {
        Exemplaire exemplaireReel = new Exemplaire();
        exemplaireReel.setStatut(StatutExemplaire.EMPRUNTE);
        exemplaireReel.setOuvrage(ouvrage);

        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setExemplaire(exemplaireReel);
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setDateRetourPrevu(LocalDate.now().plusDays(3));
        emprunt.setDateEmprunt(LocalDateTime.now().minusDays(11));
        emprunt.setProlonge(false);

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));
        when(empruntRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(exemplaireRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publishEvent(any());

        Emprunt result = empruntService.enregistrerRetour(1L);

        assertThat(result.getStatut()).isEqualTo(StatutEmprunt.RENDU);
        verify(penaliteRepo, never()).save(any());
    }

    // ── PROLONGATION ──────────────────────────────────────────

    @Test
    @DisplayName("✅ Prolongation réussie — date repoussée de 14 jours")
    void prolongerEmprunt_succes_dateRepoussee() {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setDateRetourPrevu(LocalDate.now().plusDays(5));
        emprunt.setProlonge(false);

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));
        when(configService.getDureeEmpruntJours()).thenReturn(14);
        when(empruntRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publishEvent(any());

        Emprunt result = empruntService.prolongerEmprunt(1L, 1L);

        assertThat(result.getProlonge()).isTrue();
        assertThat(result.getStatut()).isEqualTo(StatutEmprunt.PROLONGE);
        assertThat(result.getDateRetourPrevu())
                .isEqualTo(LocalDate.now().plusDays(5 + 14));
    }

    @Test
    @DisplayName("❌ Prolongation impossible — emprunt en retard")
    void prolongerEmprunt_enRetard_refus() {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setDateRetourPrevu(LocalDate.now().minusDays(2));
        emprunt.setProlonge(false);

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));

        assertThatThrownBy(() -> empruntService.prolongerEmprunt(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("retard");
    }

    // ── LECTURES ─────────────────────────────────────────────

    @Test
    @DisplayName("✅ getEmpruntsEnCours — liste vide")
    void getEmpruntsEnCours_listeVide() {
        when(empruntRepo.findEmpruntsActifsByEtudiant(1L)).thenReturn(List.of());
        assertThat(empruntService.getEmpruntsEnCours(1L)).isEmpty();
    }

    @Test
    @DisplayName("✅ getHistoriqueEtudiant — page vide")
    void getHistoriqueEtudiant_pageVide() {
        Pageable pageable = PageRequest.of(0, 10);
        when(empruntRepo.findHistoriqueByEtudiant(1L, pageable)).thenReturn(Page.empty());
        assertThat(empruntService.getHistoriqueEtudiant(1L, pageable).getContent()).isEmpty();
    }

    // ── RÈGLES MÉTIER ─────────────────────────────────────────

    @Test
    @DisplayName("✅ Quota 2/3 — emprunt autorisé")
    void creerEmprunt_quota2sur3_autorise() {
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(configService.getMaxEmpruntsSimultanes()).thenReturn(3);
        when(empruntRepo.countEmpruntsActifsByEtudiant(1L)).thenReturn(2L);
        when(configService.getSeuilBlocageFcfa()).thenReturn(500);
        when(penaliteRepo.sumMontantImpayeByEtudiantId(1L)).thenReturn(BigDecimal.ZERO);

        Exemplaire exemplaireReel = new Exemplaire();
        exemplaireReel.setStatut(StatutExemplaire.DISPONIBLE);
        exemplaireReel.setOuvrage(ouvrage);

        when(exemplaireRepo.findByIdWithLock(1L)).thenReturn(Optional.of(exemplaireReel));
        when(configService.getDureeEmpruntJours()).thenReturn(14);
        when(empruntRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(exemplaireRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publishEvent(any());

        assertThatCode(() -> empruntService.creerEmprunt(1L, 1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("✅ Pénalités à 499 FCFA — emprunt autorisé (sous le seuil)")
    void creerEmprunt_penalites499_autorise() {
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(configService.getMaxEmpruntsSimultanes()).thenReturn(3);
        when(empruntRepo.countEmpruntsActifsByEtudiant(1L)).thenReturn(0L);
        when(configService.getSeuilBlocageFcfa()).thenReturn(500);
        when(penaliteRepo.sumMontantImpayeByEtudiantId(1L)).thenReturn(BigDecimal.valueOf(499));

        Exemplaire exemplaireReel = new Exemplaire();
        exemplaireReel.setStatut(StatutExemplaire.DISPONIBLE);
        exemplaireReel.setOuvrage(ouvrage);

        when(exemplaireRepo.findByIdWithLock(1L)).thenReturn(Optional.of(exemplaireReel));
        when(configService.getDureeEmpruntJours()).thenReturn(14);
        when(empruntRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(exemplaireRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publishEvent(any());

        assertThatCode(() -> empruntService.creerEmprunt(1L, 1L))
                .doesNotThrowAnyException();
    }
}
