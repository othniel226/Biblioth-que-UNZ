package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un titre (ouvrage) dans le catalogue de la bibliothèque.
 * Un ouvrage peut avoir plusieurs exemplaires physiques ({@link Exemplaire}).
 *
 * Distinction importante :
 *   - Ouvrage = la notice bibliographique (titre, auteur, ISBN)
 *   - Exemplaire = un exemplaire physique avec un code-barres unique
 */
@Entity
@Table(name = "ouvrages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ouvrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre complet de l'ouvrage */
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String titre;

    /** ISBN-13 de l'ouvrage (unique dans le catalogue) */
    @Column(unique = true, length = 20)
    private String isbn;

    /** Nom(s) de l'auteur ou des auteurs */
    @NotBlank(message = "L'auteur est obligatoire")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String auteur;

    /** Maison d'édition */
    @Column(length = 150)
    private String editeur;

    /** Année de publication */
    @Column
    private Integer anneePublication;

    /** Description ou résumé de l'ouvrage */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** URL de l'image de couverture (optionnel) */
    @Column(length = 500)
    private String imageCouverture;

    /** Catégorie thématique de l'ouvrage */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    /**
     * Indique si l'ouvrage est archivé (retiré du catalogue actif).
     * Un ouvrage archivé n'est plus disponible à l'emprunt mais reste visible
     * dans l'historique.
     */
    @Column(nullable = false)
    private Boolean archive = false;

    /** Liste des exemplaires physiques de cet ouvrage */
    @OneToMany(mappedBy = "ouvrage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exemplaire> exemplaires = new ArrayList<>();

    /**
     * Vérifie s'il existe au moins un exemplaire disponible à l'emprunt.
     *
     * @return true si au moins un exemplaire est DISPONIBLE
     */
    public boolean estDisponible() {
        return exemplaires.stream()
            .anyMatch(e -> e.getStatut().name().equals("DISPONIBLE"));
    }

    /**
     * Retourne le nombre d'exemplaires disponibles à l'emprunt.
     *
     * @return nombre d'exemplaires avec statut DISPONIBLE
     */
    public long getNombreExemplairesDisponibles() {
        return exemplaires.stream()
            .filter(e -> e.getStatut().name().equals("DISPONIBLE"))
            .count();
    }

    /**
     * Retourne le premier exemplaire disponible (pour les emprunts).
     *
     * @return premier exemplaire DISPONIBLE, ou null si aucun
     */
    public Exemplaire getPremierExemplaireDisponible() {
        return exemplaires.stream()
            .filter(e -> e.getStatut().name().equals("DISPONIBLE"))
            .findFirst()
            .orElse(null);
    }
}
