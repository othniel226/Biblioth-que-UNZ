package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.EmpruntResponse;
import com.unz.bibliotheque.model.Emprunt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct pour les emprunts.
 */
@Mapper(componentModel = "spring")
public interface EmpruntMapper {

    @Mapping(target = "etudiantId", source = "etudiant.id")
    @Mapping(target = "etudiantNomComplet", source = "etudiant.nomComplet")
    @Mapping(target = "etudiantEmail", source = "etudiant.email")

    @Mapping(target = "exemplaireId", source = "exemplaire.id")
    @Mapping(target = "exemplaireCodeBarres", source = "exemplaire.codeBarres")

    @Mapping(target = "ouvrageId", source = "exemplaire.ouvrage.id")
    @Mapping(target = "ouvrageTitre", source = "exemplaire.ouvrage.titre")
    @Mapping(target = "ouvrageAuteur", source = "exemplaire.ouvrage.auteur")
    @Mapping(target = "ouvrageIsbn", source = "exemplaire.ouvrage.isbn")

    @Mapping(target = "enRetard", expression = "java(emprunt.estEnRetard())")
    @Mapping(target = "nombreJoursRetard", expression = "java(emprunt.getNombreJoursRetard())")

    @Mapping(target = "penaliteMontant", source = "penalite.montant")
    @Mapping(target = "penalitePayee", source = "penalite.payee")

    @Mapping(target = "bibliothecaireId", source = "bibliothecaire.id")
    @Mapping(target = "bibliothecaireNomComplet", source = "bibliothecaire.nomComplet")

    EmpruntResponse toResponse(Emprunt emprunt);
}
