package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.OuvrageResponse;
import com.unz.bibliotheque.model.Categorie;
import com.unz.bibliotheque.model.Ouvrage;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-30T20:08:17+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        ouvrageResponse.anneePublication( ouvrage.getAnneePublication() );
        ouvrageResponse.archive( ouvrage.getArchive() );
        ouvrageResponse.auteur( ouvrage.getAuteur() );
        ouvrageResponse.description( ouvrage.getDescription() );
        ouvrageResponse.editeur( ouvrage.getEditeur() );
        ouvrageResponse.id( ouvrage.getId() );
        ouvrageResponse.imageCouverture( ouvrage.getImageCouverture() );
        ouvrageResponse.isbn( ouvrage.getIsbn() );
        ouvrageResponse.titre( ouvrage.getTitre() );

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
