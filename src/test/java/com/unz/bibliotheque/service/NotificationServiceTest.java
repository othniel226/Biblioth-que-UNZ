package com.unz.bibliotheque.service;

import com.unz.bibliotheque.event.EmpruntCreeEvent;
import com.unz.bibliotheque.event.EmpruntProlongeEvent;
import com.unz.bibliotheque.event.ExemplaireRetourneEvent;
import com.unz.bibliotheque.model.*;
import com.unz.bibliotheque.model.enums.TypeNotification;
import com.unz.bibliotheque.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour NotificationService.
 * Pattern : Observer (événements Spring)
 * Couverture : onEmpruntCree, onExemplaireRetourne, envoyerConfirmationEmprunt,
 *              envoyerConfirmationRetour, envoyerRappelRetour, envoyerAlerteRetard,
 *              envoyerDisponibiliteReservation, envoyerConfirmationProlongation
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Etudiant etudiant;
    private Ouvrage ouvrage;
    private Exemplaire exemplaire;
    private Emprunt emprunt;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Ouédraogo");
        etudiant.setPrenom("Moussa");
        etudiant.setEmail("moussa.ouedraogo@etud.unz.bf");

        ouvrage = new Ouvrage();
        ouvrage.setId(1L);
        ouvrage.setTitre("Introduction au Genie Logiciel");

        exemplaire = new Exemplaire();
        exemplaire.setId(1L);
        exemplaire.setCodeBarres("EX-001-A");
        exemplaire.setOuvrage(ouvrage);

        emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setEtudiant(etudiant);
        emprunt.setExemplaire(exemplaire);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setEtudiant(etudiant);
        reservation.setOuvrage(ouvrage);
    }

    // ══════════════════════════════════════════════════════
    // TEST 1 : onEmpruntCree — écoute d'événement
    // ══════════════════════════════════════════════════════
    @Test
    void onEmpruntCree_avecEvent_doitEnvoyerEmail() {
        // ARRANGE
        EmpruntCreeEvent event = new EmpruntCreeEvent(this, emprunt);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.onEmpruntCree(event);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 2 : onExemplaireRetourne — écoute d'événement
    // ══════════════════════════════════════════════════════
    @Test
    void onExemplaireRetourne_avecEvent_doitEnvoyerEmail() {
        // ARRANGE
        ExemplaireRetourneEvent event = new ExemplaireRetourneEvent(this, emprunt);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.onExemplaireRetourne(event);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 3 : envoyerConfirmationEmprunt
    // ══════════════════════════════════════════════════════
    @Test
    void envoyerConfirmationEmprunt_avecEmpruntValide_doitEnvoyerEmail() {
        // ARRANGE
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.envoyerConfirmationEmprunt(emprunt);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 4 : envoyerConfirmationRetour — sans pénalité
    // ══════════════════════════════════════════════════════
    @Test
    void envoyerConfirmationRetour_sansPenalite_doitEnvoyerEmail() {
        // ARRANGE
        emprunt.setPenalite(null);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.envoyerConfirmationRetour(emprunt);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 5 : envoyerRappelRetour
    // ══════════════════════════════════════════════════════
    @Test
    void envoyerRappelRetour_avecEmpruntValide_doitEnvoyerEmail() {
        // ARRANGE
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.envoyerRappelRetour(emprunt);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 6 : envoyerAlerteRetard
    // ══════════════════════════════════════════════════════
    @Test
    void envoyerAlerteRetard_avecEmpruntEnRetard_doitEnvoyerEmail() {
        // ARRANGE
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.envoyerAlerteRetard(emprunt);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 7 : envoyerDisponibiliteReservation
    // ══════════════════════════════════════════════════════
    @Test
    void envoyerDisponibiliteReservation_avecReservationValide_doitEnvoyerEmail() {
        // ARRANGE
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.envoyerDisponibiliteReservation(reservation);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }

    // ══════════════════════════════════════════════════════
    // TEST 8 : envoyerConfirmationProlongation
    // ══════════════════════════════════════════════════════
    @Test
    void envoyerConfirmationProlongation_avecEmpruntValide_doitEnvoyerEmail() {
        // ARRANGE
        EmpruntProlongeEvent event = new EmpruntProlongeEvent(this, emprunt);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any())).thenReturn(null);

        // ACT
        notificationService.onEmpruntProlonge(event);

        // ASSERT
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any());
    }
}
