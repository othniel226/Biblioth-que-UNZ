package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un étudiant de l'Université Norbert Zongo.
 * Hérite de {@link Utilisateur} avec la stratégie JOINED.
 *
 * Un étudiant peut :
 *   - emprunter jusqu'à 3 ouvrages simultanément
 *   - réserver des ouvrages indisponibles (file FIFO)
 *   - consulter son historique d'emprunts
 *   - demander une prolongation (une seule fois par emprunt)
 */
@Entity
@Table(name = "etudiants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant extends Utilisateur {

    /** Matricule universitaire unique de l'étudiant (ex: 2023INF001) */
    @NotBlank(message = "Le matricule est obligatoire")
    @Column(nullable = false, unique = true, length = 20)
    private String matricule;

    /** Filière d'étude (ex: Informatique, Mathématiques, Physique) */
    @NotBlank(message = "La filière est obligatoire")
    @Column(nullable = false, length = 100)
    private String filiere;

    /** Niveau académique (ex: L1, L2, L3, M1, M2) */
    @NotBlank(message = "Le niveau est obligatoire")
    @Column(nullable = false, length = 10)
    private String niveau;

    /**
     * Liste de tous les emprunts de cet étudiant.
     * Relation bidirectionnelle mappée par le champ "etudiant" dans Emprunt.
     */
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprunt> emprunts = new ArrayList<>();

    /**
     * Liste de toutes les réservations de cet étudiant.
     */
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Retourne le nombre d'emprunts actifs (EN_COURS ou EN_RETARD ou PROLONGE).
     * Utilisé pour vérifier le quota (max 3 emprunts simultanés).
     *
     * @return nombre d'emprunts en cours
     */
    public long getNombreEmpruntsActifs() {
        return emprunts.stream()
            .filter(e -> e.getStatut() != null && e.getStatut().name().equals("RENDU") == false)
            .count();
    }
}
