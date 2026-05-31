package com.unz.bibliotheque.service;

import com.unz.bibliotheque.exception.BusinessException;
import com.unz.bibliotheque.exception.ResourceNotFoundException;
import com.unz.bibliotheque.exception.UnauthorizedException;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.StatutReservation;
import com.unz.bibliotheque.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ReservationService — Tests des règles métier")
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepo;
    @Mock private OuvrageRepository     ouvrageRepo;
    @Mock private EtudiantRepository    etudiantRepo;
    @Mock private ExemplaireRepository  exemplaireRepo;
    @Mock private ConfigurationService  configService;
    @Mock private NotificationService   notificationService;

    @InjectMocks
    private ReservationService reservationService;

    private Etudiant etudiant;
    private Ouvrage ouvrage;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setEmail("moussa.ouedraogo@etud.unz.bf");
        etudiant.setActif(true);

        ouvrage = mock(Ouvrage.class);
        lenient().when(ouvrage.getId()).thenReturn(1L);
    }

    // ── CRÉER RÉSERVATION ─────────────────────────────────────

    @Test
    @DisplayName("❌ Réservation refusée — ouvrage introuvable")
    void creerReservation_ouvrageIntrouvable() {
        when(ouvrageRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.creerReservation(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("❌ Réservation refusée — étudiant introuvable")
    void creerReservation_etudiantIntrouvable() {
        when(ouvrageRepo.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(etudiantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.creerReservation(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("❌ Réservation refusée — des exemplaires sont disponibles")
    void creerReservation_ouvrageDisponible() {
        when(ouvrageRepo.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(ouvrage.estDisponible()).thenReturn(true);

        assertThatThrownBy(() -> reservationService.creerReservation(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("disponible");
    }

    @Test
    @DisplayName("❌ Réservation refusée — doublon (déjà une réservation active)")
    void creerReservation_doublon() {
        when(ouvrageRepo.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(ouvrage.estDisponible()).thenReturn(false);
        when(reservationRepo.countActiveByEtudiantAndOuvrage(1L, 1L)).thenReturn(1L);

        assertThatThrownBy(() -> reservationService.creerReservation(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà");
    }

    @Test
    @DisplayName("❌ Réservation refusée — quota de réservations atteint")
    void creerReservation_quotaAtteint() {
        when(ouvrageRepo.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(ouvrage.estDisponible()).thenReturn(false);
        when(reservationRepo.countActiveByEtudiantAndOuvrage(1L, 1L)).thenReturn(0L);
        when(configService.getMaxReservationsSimultanes()).thenReturn(2);
        when(reservationRepo.findByEtudiantIdAndStatutIn(eq(1L), anyList()))
                .thenReturn(List.of(new Reservation(), new Reservation()));

        assertThatThrownBy(() -> reservationService.creerReservation(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Quota");
    }

    @Test
    @DisplayName("✅ Réservation créée avec succès")
    void creerReservation_succes() {
        when(ouvrageRepo.findById(1L)).thenReturn(Optional.of(ouvrage));
        when(etudiantRepo.findById(1L)).thenReturn(Optional.of(etudiant));
        when(ouvrage.estDisponible()).thenReturn(false);
        when(reservationRepo.countActiveByEtudiantAndOuvrage(1L, 1L)).thenReturn(0L);
        when(configService.getMaxReservationsSimultanes()).thenReturn(5);
        when(reservationRepo.findByEtudiantIdAndStatutIn(eq(1L), anyList()))
                .thenReturn(List.of());
        when(reservationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation r = reservationService.creerReservation(1L, 1L);

        assertThat(r).isNotNull();
        assertThat(r.getStatut()).isEqualTo(StatutReservation.EN_ATTENTE);
    }

    // ── ANNULER RÉSERVATION ───────────────────────────────────

    @Test
    @DisplayName("❌ Annulation impossible — réservation introuvable")
    void annulerReservation_introuvable() {
        when(reservationRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.annulerReservation(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("❌ Annulation impossible — réservation appartient à un autre étudiant")
    void annulerReservation_autreEtudiant() {
        Etudiant autreEtudiant = new Etudiant();
        autreEtudiant.setId(99L);

        Reservation r = new Reservation();
        r.setId(1L);
        r.setEtudiant(autreEtudiant);
        r.setStatut(StatutReservation.EN_ATTENTE);

        when(reservationRepo.findById(1L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> reservationService.annulerReservation(1L, 1L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("❌ Annulation impossible — statut EXPIREE")
    void annulerReservation_statutExpiree() {
        Reservation r = new Reservation();
        r.setId(1L);
        r.setEtudiant(etudiant);
        r.setStatut(StatutReservation.EXPIREE);

        when(reservationRepo.findById(1L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> reservationService.annulerReservation(1L, 1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("❌ Annulation impossible — statut TRANSFORMEE")
    void annulerReservation_statutTransformee() {
        Reservation r = new Reservation();
        r.setId(1L);
        r.setEtudiant(etudiant);
        r.setStatut(StatutReservation.TRANSFORMEE);

        when(reservationRepo.findById(1L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> reservationService.annulerReservation(1L, 1L))
                .isInstanceOf(BusinessException.class);
    }

    // ── LECTURES ─────────────────────────────────────────────

    @Test
    @DisplayName("✅ getReservationsActives retourne EN_ATTENTE et CONFIRMEE")
    void getReservationsActives_retourneListe() {
        Reservation r1 = new Reservation(); r1.setStatut(StatutReservation.EN_ATTENTE);
        Reservation r2 = new Reservation(); r2.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepo.findByEtudiantIdAndStatutIn(eq(1L), anyList()))
                .thenReturn(List.of(r1, r2));

        List<Reservation> result = reservationService.getReservationsActives(1L);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("✅ getReservationsActives retourne liste vide si aucune réservation")
    void getReservationsActives_listeVide() {
        when(reservationRepo.findByEtudiantIdAndStatutIn(eq(1L), anyList()))
                .thenReturn(List.of());

        List<Reservation> result = reservationService.getReservationsActives(1L);
        assertThat(result).isEmpty();
    }
}
