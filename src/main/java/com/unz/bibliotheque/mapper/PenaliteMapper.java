package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.PenaliteResponse;
import com.unz.bibliotheque.model.Penalite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct pour les pénalités.
 */
@Mapper(componentModel = "spring")
public interface PenaliteMapper {

    @Mapping(target = "empruntId", source = "emprunt.id")
    @Mapping(target = "ouvrageTitre", source = "emprunt.exemplaire.ouvrage.titre")
    @Mapping(target = "etudiantNomComplet", source = "emprunt.etudiant.nomComplet")

    PenaliteResponse toResponse(Penalite penalite);
}
