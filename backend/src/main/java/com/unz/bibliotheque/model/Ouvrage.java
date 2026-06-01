package com.unz.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ouvrages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ouvrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String titre;

    @Column(unique = true, length = 20)
    private String isbn;

    @NotBlank(message = "L'auteur est obligatoire")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String auteur;

    @Column(length = 150)
    private String editeur;

    @Column
    private Integer anneePublication;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String imageCouverture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @Column(nullable = false)
    private Boolean archive = false;

    // ── EAGER pour que estDisponible() fonctionne hors transaction ───
    @OneToMany(mappedBy = "ouvrage", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Exemplaire> exemplaires = new ArrayList<>();

    public boolean estDisponible() {
        if (exemplaires == null) return false;
        return exemplaires.stream()
            .anyMatch(e -> e.getStatut() != null && e.getStatut().name().equals("DISPONIBLE"));
    }

    public long getNombreExemplairesDisponibles() {
        if (exemplaires == null) return 0;
        return exemplaires.stream()
            .filter(e -> e.getStatut() != null && e.getStatut().name().equals("DISPONIBLE"))
            .count();
    }

    public Exemplaire getPremierExemplaireDisponible() {
        if (exemplaires == null) return null;
        return exemplaires.stream()
            .filter(e -> e.getStatut() != null && e.getStatut().name().equals("DISPONIBLE"))
            .findFirst()
            .orElse(null);
    }
}
