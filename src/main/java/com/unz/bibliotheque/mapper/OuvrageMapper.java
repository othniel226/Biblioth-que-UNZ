package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.OuvrageResponse;
import com.unz.bibliotheque.model.Ouvrage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct pour les ouvrages.
 */
@Mapper(componentModel = "spring")
public interface OuvrageMapper {

    @Mapping(target = "categorieId", source = "categorie.id")
    @Mapping(target = "categorieNom", source = "categorie.nom")

    @Mapping(target = "disponible", expression = "java(ouvrage.estDisponible())")
    @Mapping(target = "nombreExemplairesDisponibles", expression = "java(ouvrage.getNombreExemplairesDisponibles())")
    @Mapping(target = "nombreExemplairesTotal", expression = "java(ouvrage.getExemplaires() != null ? Long.valueOf(ouvrage.getExemplaires().size()) : 0L)")

    OuvrageResponse toResponse(Ouvrage ouvrage);
}
