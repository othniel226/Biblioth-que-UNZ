package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.PenaliteResponse;
import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Exemplaire;
import com.unz.bibliotheque.model.Ouvrage;
import com.unz.bibliotheque.model.Penalite;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-30T20:08:20+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PenaliteMapperImpl implements PenaliteMapper {

    @Override
    public PenaliteResponse toResponse(Penalite penalite) {
        if ( penalite == null ) {
            return null;
        }

        PenaliteResponse.PenaliteResponseBuilder penaliteResponse = PenaliteResponse.builder();

        penaliteResponse.empruntId( penaliteEmpruntId( penalite ) );
        penaliteResponse.ouvrageTitre( penaliteEmpruntExemplaireOuvrageTitre( penalite ) );
        penaliteResponse.etudiantNomComplet( penaliteEmpruntEtudiantNomComplet( penalite ) );
        penaliteResponse.dateCreation( penalite.getDateCreation() );
        penaliteResponse.datePaiement( penalite.getDatePaiement() );
        penaliteResponse.id( penalite.getId() );
        penaliteResponse.joursRetard( penalite.getJoursRetard() );
        penaliteResponse.montant( penalite.getMontant() );
        penaliteResponse.payee( penalite.getPayee() );
        penaliteResponse.strategieUtilisee( penalite.getStrategieUtilisee() );

        return penaliteResponse.build();
    }

    private Long penaliteEmpruntId(Penalite penalite) {
        if ( penalite == null ) {
            return null;
        }
        Emprunt emprunt = penalite.getEmprunt();
        if ( emprunt == null ) {
            return null;
        }
        Long id = emprunt.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String penaliteEmpruntExemplaireOuvrageTitre(Penalite penalite) {
        if ( penalite == null ) {
            return null;
        }
        Emprunt emprunt = penalite.getEmprunt();
        if ( emprunt == null ) {
            return null;
        }
        Exemplaire exemplaire = emprunt.getExemplaire();
        if ( exemplaire == null ) {
            return null;
        }
        Ouvrage ouvrage = exemplaire.getOuvrage();
        if ( ouvrage == null ) {
            return null;
        }
        String titre = ouvrage.getTitre();
        if ( titre == null ) {
            return null;
        }
        return titre;
    }

    private String penaliteEmpruntEtudiantNomComplet(Penalite penalite) {
        if ( penalite == null ) {
            return null;
        }
        Emprunt emprunt = penalite.getEmprunt();
        if ( emprunt == null ) {
            return null;
        }
        Etudiant etudiant = emprunt.getEtudiant();
        if ( etudiant == null ) {
            return null;
        }
        String nomComplet = etudiant.getNomComplet();
        if ( nomComplet == null ) {
            return null;
        }
        return nomComplet;
    }
}
