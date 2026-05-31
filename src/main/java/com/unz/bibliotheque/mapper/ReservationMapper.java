package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.ReservationResponse;
import com.unz.bibliotheque.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct pour les réservations.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "etudiantId", source = "etudiant.id")
    @Mapping(target = "etudiantNomComplet", source = "etudiant.nomComplet")

    @Mapping(target = "ouvrageId", source = "ouvrage.id")
    @Mapping(target = "ouvrageTitre", source = "ouvrage.titre")
    @Mapping(target = "ouvrageAuteur", source = "ouvrage.auteur")

    ReservationResponse toResponse(Reservation reservation);
}
