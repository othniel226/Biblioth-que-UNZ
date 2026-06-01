package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entité représentant une notification envoyée à un utilisateur.
 * Les notifications sont envoyées par email via le Service de notification.
 *
 * Générées par le Pattern Observer lors d'événements métier :
 *   - Création d'emprunt → CONFIRMATION_EMPRUNT
 *   - J-3 avant retour → RAPPEL_RETOUR
 *   - Retard détecté → ALERTE_RETARD
 *   - Réservation disponible → RESERVATION_DISPO
 */
@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Utilisateur destinataire de la notification */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    /** Type de notification (détermine le template email utilisé) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeNotification type;

    /** Sujet de l'email envoyé */
    @Column(nullable = false, length = 200)
    private String sujet;

    /** Corps du message (texte ou HTML) */
    @Column(columnDefinition = "TEXT")
    private String message;

    /** Indique si la notification a été lue dans l'interface */
    @Column(nullable = false)
    private Boolean lu = false;

    /** Date et heure d'envoi de la notification */
    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    /**
     * Marque la notification comme lue.
     */
    public void marquerLu() {
        this.lu = true;
    }
}
