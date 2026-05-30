package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutEmprunt;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entité représentant un emprunt d'ouvrage.
 *
 * Un emprunt lie un étudiant à un exemplaire physique pour une durée définie.
 * La durée par défaut est configurée dans ConfigurationSysteme (DUREE_EMPRUNT_JOURS).
 *
 * Cycle de vie : EN_COURS → PROLONGE (optionnel) → RENDU
 *                           ↓ si retard
 *                         EN_RETARD → RENDU
 *
 * Design Pattern : l'emprunt génère des événements Spring (Pattern Observer)
 * lors de sa création, prolongation et retour.
 */
@Entity
@Table(name = "emprunts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Étudiant qui a effectué l'emprunt */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    /** Exemplaire physique emprunté */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exemplaire_id", nullable = false)
    private Exemplaire exemplaire;

    /** Date et heure de création de l'emprunt */
    @Column(nullable = false)
    private LocalDateTime dateEmprunt;

    /** Date limite de retour (calculée automatiquement selon la configuration) */
    @Column(nullable = false)
    private LocalDate dateRetourPrevu;

    /** Date effective du retour (null tant que l'ouvrage n'est pas rendu) */
    @Column
    private LocalDate dateRetourReel;

    /** Statut actuel de l'emprunt */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEmprunt statut = StatutEmprunt.EN_COURS;

    /** Indique si l'emprunt a été prolongé (une seule prolongation autorisée) */
    @Column(nullable = false)
    private Boolean prolonge = false;

    /** Bibliothécaire ayant enregistré cet emprunt (null si self-service) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bibliothecaire_id")
    private Bibliothecaire bibliothecaire;

    /** Pénalité associée à cet emprunt (null si pas de retard ou déjà payée) */
    @OneToOne(mappedBy = "emprunt", cascade = CascadeType.ALL)
    private Penalite penalite;

    /**
     * Vérifie si l'emprunt est en retard par rapport à la date actuelle.
     *
     * @return true si la date de retour prévue est passée et l'ouvrage non rendu
     */
    public boolean estEnRetard() {
        return statut != StatutEmprunt.RENDU
            && dateRetourPrevu != null
            && LocalDate.now().isAfter(dateRetourPrevu);
    }

    /**
     * Calcule le nombre de jours de retard.
     *
     * @return nombre de jours de retard (0 si pas en retard)
     */
    public long getNombreJoursRetard() {
        if (!estEnRetard()) return 0;
        LocalDate fin = dateRetourReel != null ? dateRetourReel : LocalDate.now();
        return ChronoUnit.DAYS.between(dateRetourPrevu, fin);
    }

    /**
     * Clôture l'emprunt en enregistrant la date de retour réelle.
     * Met à jour le statut à RENDU.
     *
     * @param dateRetour date effective du retour
     */
    public void clore(LocalDate dateRetour) {
        this.dateRetourReel = dateRetour;
        this.statut = StatutEmprunt.RENDU;
    }
}
