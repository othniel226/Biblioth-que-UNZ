package com.unz.bibliotheque.service;

import com.unz.bibliotheque.exception.BusinessException;
import com.unz.bibliotheque.exception.ResourceNotFoundException;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.pattern.strategy.PenaliteStrategy;
import com.unz.bibliotheque.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
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
@DisplayName("EmpruntService — Tests des règles métier")
class EmpruntServiceTest {

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
    private Exemplaire exemplaire;
    private Ouvrage ouvrage;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Ouedraogo");
        etudiant.setPrenom("Moussa");
        etudiant.setEmail("moussa.ouedraogo@etud.unz.bf");
        etudiant.setActif(true);

        ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Génie Logiciel");

        exemplaire = new Exemplaire();
        exemplaire.setId(1L);
        exemplaire.setOuvrage(ouvrage);
    }

    // ── helpers ──────────────────────────────────────────────

    private void mockExemplaireDispo() {
        when(exemplaireRepo.findByIdWithLock(1L)).thenReturn(Optional.of(exemplaire));
        doReturn("DISPONIBLE").when(exemplaire.getStatut() != null ? exemplaire : exemplaire)
            .toString(); // on utilise une autre approche ci-dessous
    }

    // ── CRÉER EMPRUNT ────────────────────────────────────────

    @Test
    @DisplayName("❌ Emprunt refusé — étudiant introuvable")
    void creerEmprunt_etudiantIntrouvable() {
        when(etudiantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empruntService.creerEmprunt(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("❌ Emprunt refusé — compte étudiant désactivé (Règle 1)")
    void creerEmprunt_compteDesactive() {
        etudiant.setActif(false);
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() -> empruntService.creerEmprunt(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("désactivé");
    }

    @Test
    @DisplayName("❌ Emprunt refusé — quota d'emprunts atteint (Règle 2)")
    void creerEmprunt_quotaAtteint() {
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(configService.getMaxEmpruntsSimultanes()).thenReturn(3);
        when(empruntRepo.countEmpruntsActifsByEtudiant(1L)).thenReturn(3L);

        assertThatThrownBy(() -> empruntService.creerEmprunt(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Quota");
    }

    @Test
    @DisplayName("❌ Emprunt refusé — pénalités >= seuil de blocage (Règle 3)")
    void creerEmprunt_penalitesBloquantes() {
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(configService.getMaxEmpruntsSimultanes()).thenReturn(3);
        when(empruntRepo.countEmpruntsActifsByEtudiant(1L)).thenReturn(1L);
        when(configService.getSeuilBlocageFcfa()).thenReturn(500);
        when(penaliteRepo.sumMontantImpayeByEtudiantId(1L)).thenReturn(BigDecimal.valueOf(600));

        assertThatThrownBy(() -> empruntService.creerEmprunt(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqué");
    }

    @Test
    @DisplayName("❌ Emprunt refusé — exemplaire introuvable (Règle 4)")
    void creerEmprunt_exemplaireIntrouvable() {
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(configService.getMaxEmpruntsSimultanes()).thenReturn(3);
        when(empruntRepo.countEmpruntsActifsByEtudiant(1L)).thenReturn(0L);
        when(configService.getSeuilBlocageFcfa()).thenReturn(500);
        when(penaliteRepo.sumMontantImpayeByEtudiantId(1L)).thenReturn(BigDecimal.ZERO);
        when(exemplaireRepo.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empruntService.creerEmprunt(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── ENREGISTRER RETOUR ────────────────────────────────────

    @Test
    @DisplayName("❌ Retour impossible — emprunt introuvable")
    void enregistrerRetour_empruntIntrouvable() {
        when(empruntRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empruntService.enregistrerRetour(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("❌ Retour impossible — emprunt déjà rendu")
    void enregistrerRetour_dejaRendu() {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setStatut(StatutEmprunt.RENDU);

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));

        assertThatThrownBy(() -> empruntService.enregistrerRetour(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà");
    }

    // ── PROLONGER ────────────────────────────────────────────

    @Test
    @DisplayName("❌ Prolongation impossible — emprunt introuvable")
    void prolongerEmprunt_introuvable() {
        when(empruntRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empruntService.prolongerEmprunt(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("❌ Prolongation impossible — emprunt n'appartient pas à l'étudiant")
    void prolongerEmprunt_pasLeBonEtudiant() {
        Etudiant autreEtudiant = new Etudiant();
        autreEtudiant.setId(99L);

        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(autreEtudiant);
        emprunt.setStatut(StatutEmprunt.EN_COURS);

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));

        assertThatThrownBy(() -> empruntService.prolongerEmprunt(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("appartient pas");
    }

    @Test
    @DisplayName("❌ Prolongation impossible — déjà prolongé")
    void prolongerEmprunt_dejaProlong() {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setProlonge(true);
        emprunt.setStatut(StatutEmprunt.PROLONGE);

        when(empruntRepo.findById(1L)).thenReturn(Optional.of(emprunt));

        assertThatThrownBy(() -> empruntService.prolongerEmprunt(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà été prolongé");
    }

    // ── LECTURES ─────────────────────────────────────────────

    @Test
    @DisplayName("✅ getEmpruntsEnCours retourne la liste des emprunts actifs")
    void getEmpruntsEnCours_retourneListe() {
        when(empruntRepo.findEmpruntsActifsByEtudiant(1L))
                .thenReturn(List.of(new Emprunt(), new Emprunt()));

        List<Emprunt> result = empruntService.getEmpruntsEnCours(1L);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("✅ getHistoriqueEtudiant retourne une page d'emprunts")
    void getHistoriqueEtudiant_retournePage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Emprunt> page = new PageImpl<>(List.of(new Emprunt()));
        when(empruntRepo.findHistoriqueByEtudiant(1L, pageable)).thenReturn(page);

        Page<Emprunt> result = empruntService.getHistoriqueEtudiant(1L, pageable);
        assertThat(result.getContent()).hasSize(1);
    }
}
