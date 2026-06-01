package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.OuvrageResponse;
import com.unz.bibliotheque.model.Categorie;
import com.unz.bibliotheque.model.Ouvrage;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T20:56:55+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class OuvrageMapperImpl implements OuvrageMapper {

    @Override
    public OuvrageResponse toResponse(Ouvrage ouvrage) {
        if ( ouvrage == null ) {
            return null;
        }

        OuvrageResponse.OuvrageResponseBuilder ouvrageResponse = OuvrageResponse.builder();

        ouvrageResponse.categorieId( ouvrageCategorieId( ouvrage ) );
        ouvrageResponse.categorieNom( ouvrageCategorieNom( ouvrage ) );
        ouvrageResponse.id( ouvrage.getId() );
        ouvrageResponse.titre( ouvrage.getTitre() );
        ouvrageResponse.isbn( ouvrage.getIsbn() );
        ouvrageResponse.auteur( ouvrage.getAuteur() );
        ouvrageResponse.editeur( ouvrage.getEditeur() );
        ouvrageResponse.anneePublication( ouvrage.getAnneePublication() );
        ouvrageResponse.description( ouvrage.getDescription() );
        ouvrageResponse.imageCouverture( ouvrage.getImageCouverture() );
        ouvrageResponse.archive( ouvrage.getArchive() );

        ouvrageResponse.disponible( ouvrage.estDisponible() );
        ouvrageResponse.nombreExemplairesDisponibles( ouvrage.getNombreExemplairesDisponibles() );
        ouvrageResponse.nombreExemplairesTotal( ouvrage.getExemplaires() != null ? Long.valueOf(ouvrage.getExemplaires().size()) : 0L );

        return ouvrageResponse.build();
    }

    private Long ouvrageCategorieId(Ouvrage ouvrage) {
        if ( ouvrage == null ) {
            return null;
        }
        Categorie categorie = ouvrage.getCategorie();
        if ( categorie == null ) {
            return null;
        }
        Long id = categorie.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String ouvrageCategorieNom(Ouvrage ouvrage) {
        if ( ouvrage == null ) {
            return null;
        }
        Categorie categorie = ouvrage.getCategorie();
        if ( categorie == null ) {
            return null;
        }
        String nom = categorie.getNom();
        if ( nom == null ) {
            return null;
        }
        return nom;
    }
}
