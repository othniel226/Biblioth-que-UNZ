package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.ReservationResponse;
import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Ouvrage;
import com.unz.bibliotheque.model.Reservation;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T20:56:58+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class ReservationMapperImpl implements ReservationMapper {

    @Override
    public ReservationResponse toResponse(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }

        ReservationResponse.ReservationResponseBuilder reservationResponse = ReservationResponse.builder();

        reservationResponse.etudiantId( reservationEtudiantId( reservation ) );
        reservationResponse.etudiantNomComplet( reservationEtudiantNomComplet( reservation ) );
        reservationResponse.ouvrageId( reservationOuvrageId( reservation ) );
        reservationResponse.ouvrageTitre( reservationOuvrageTitre( reservation ) );
        reservationResponse.ouvrageAuteur( reservationOuvrageAuteur( reservation ) );
        reservationResponse.id( reservation.getId() );
        reservationResponse.statut( reservation.getStatut() );
        reservationResponse.dateReservation( reservation.getDateReservation() );
        reservationResponse.dateConfirmation( reservation.getDateConfirmation() );
        reservationResponse.dateExpiration( reservation.getDateExpiration() );
        reservationResponse.positionFile( reservation.getPositionFile() );

        return reservationResponse.build();
    }

    private Long reservationEtudiantId(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }
        Etudiant etudiant = reservation.getEtudiant();
        if ( etudiant == null ) {
            return null;
        }
        Long id = etudiant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String reservationEtudiantNomComplet(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }
        Etudiant etudiant = reservation.getEtudiant();
        if ( etudiant == null ) {
            return null;
        }
        String nomComplet = etudiant.getNomComplet();
        if ( nomComplet == null ) {
            return null;
        }
        return nomComplet;
    }

    private Long reservationOuvrageId(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }
        Ouvrage ouvrage = reservation.getOuvrage();
        if ( ouvrage == null ) {
            return null;
        }
        Long id = ouvrage.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String reservationOuvrageTitre(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }
        Ouvrage ouvrage = reservation.getOuvrage();
        if ( ouvrage == null ) {
            return null;
        }
        String titre = ouvrage.getTitre();
        if ( titre == null ) {
            return null;
        }
        return titre;
    }

    private String reservationOuvrageAuteur(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }
        Ouvrage ouvrage = reservation.getOuvrage();
        if ( ouvrage == null ) {
            return null;
        }
        String auteur = ouvrage.getAuteur();
        if ( auteur == null ) {
            return null;
        }
        return auteur;
    }
}
