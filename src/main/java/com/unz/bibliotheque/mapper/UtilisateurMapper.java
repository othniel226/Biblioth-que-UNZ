package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.request.UtilisateurRequest;
import com.unz.bibliotheque.dto.response.UtilisateurResponse;
import com.unz.bibliotheque.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper MapStruct pour les utilisateurs.
 * Gère la conversion entre entités et DTOs pour tous les types d'utilisateurs.
 */
@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    /**
     * Convertit un Utilisateur (quel que soit le sous-type) en UtilisateurResponse.
     * Les champs spécifiques sont mappés conditionnellement.
     */
    @Mapping(target = "matricule", source = ".", qualifiedByName = "extractMatricule")
    @Mapping(target = "filiere", source = ".", qualifiedByName = "extractFiliere")
    @Mapping(target = "niveau", source = ".", qualifiedByName = "extractNiveau")
    @Mapping(target = "badgeNumero", source = ".", qualifiedByName = "extractBadgeNumero")
    @Mapping(target = "service", source = ".", qualifiedByName = "extractService")
    @Mapping(target = "departement", source = ".", qualifiedByName = "extractDepartement")
    UtilisateurResponse toResponse(Utilisateur utilisateur);

    /**
     * Convertit une requête en Etudiant.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @Mapping(target = "actif", constant = "true")
    @Mapping(target = "role", expression = "java(com.unz.bibliotheque.model.enums.Role.ETUDIANT)")
    @Mapping(target = "emprunts", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    Etudiant toEtudiant(UtilisateurRequest request);

    /**
     * Convertit une requête en Bibliothecaire.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @Mapping(target = "actif", constant = "true")
    @Mapping(target = "role", expression = "java(com.unz.bibliotheque.model.enums.Role.BIBLIOTHECAIRE)")
    Bibliothecaire toBibliothecaire(UtilisateurRequest request);

    /**
     * Convertit une requête en Administrateur.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @Mapping(target = "actif", constant = "true")
    @Mapping(target = "role", expression = "java(com.unz.bibliotheque.model.enums.Role.ADMINISTRATEUR)")
    Administrateur toAdministrateur(UtilisateurRequest request);

    // ══════════════════════════════════════════════════════
    // MÉTHODES NAMED — Extraction des champs spécifiques
    // ══════════════════════════════════════════════════════

    @Named("extractMatricule")
    default String extractMatricule(Utilisateur u) {
        return u instanceof Etudiant ? ((Etudiant) u).getMatricule() : null;
    }

    @Named("extractFiliere")
    default String extractFiliere(Utilisateur u) {
        return u instanceof Etudiant ? ((Etudiant) u).getFiliere() : null;
    }

    @Named("extractNiveau")
    default String extractNiveau(Utilisateur u) {
        return u instanceof Etudiant ? ((Etudiant) u).getNiveau() : null;
    }

    @Named("extractBadgeNumero")
    default String extractBadgeNumero(Utilisateur u) {
        return u instanceof Bibliothecaire ? ((Bibliothecaire) u).getBadgeNumero() : null;
    }

    @Named("extractService")
    default String extractService(Utilisateur u) {
        return u instanceof Bibliothecaire ? ((Bibliothecaire) u).getService() : null;
    }

    @Named("extractDepartement")
    default String extractDepartement(Utilisateur u) {
        return u instanceof Administrateur ? ((Administrateur) u).getDepartement() : null;
    }
}
