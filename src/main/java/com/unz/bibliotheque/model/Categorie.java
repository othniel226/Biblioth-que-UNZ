package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Catégorie thématique pour classifier les ouvrages.
 * Exemples : Informatique, Mathématiques, Littérature, Sciences, Histoire...
 */
@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de la catégorie (unique dans le système) */
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    /** Description de la catégorie */
    @Column(length = 500)
    private String description;

    /** Couleur hexadécimale pour l'affichage UI (ex: #1a237e) */
    @Column(length = 10)
    private String couleur = "#1a237e";

    /** Liste des ouvrages appartenant à cette catégorie */
    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    private List<Ouvrage> ouvrages = new ArrayList<>();
}
