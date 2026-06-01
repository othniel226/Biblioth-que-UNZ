package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.EmpruntResponse;
import com.unz.bibliotheque.model.Bibliothecaire;
import com.unz.bibliotheque.model.Emprunt;
import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Exemplaire;
import com.unz.bibliotheque.model.Ouvrage;
import com.unz.bibliotheque.model.Penalite;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T20:56:58+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class EmpruntMapperImpl implements EmpruntMapper {

    @Override
    public EmpruntResponse toResponse(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }

        EmpruntResponse.EmpruntResponseBuilder empruntResponse = EmpruntResponse.builder();

        empruntResponse.etudiantId( empruntEtudiantId( emprunt ) );
        empruntResponse.etudiantNomComplet( empruntEtudiantNomComplet( emprunt ) );
        empruntResponse.etudiantEmail( empruntEtudiantEmail( emprunt ) );
        empruntResponse.exemplaireId( empruntExemplaireId( emprunt ) );
        empruntResponse.exemplaireCodeBarres( empruntExemplaireCodeBarres( emprunt ) );
        empruntResponse.ouvrageId( empruntExemplaireOuvrageId( emprunt ) );
        empruntResponse.ouvrageTitre( empruntExemplaireOuvrageTitre( emprunt ) );
        empruntResponse.ouvrageAuteur( empruntExemplaireOuvrageAuteur( emprunt ) );
        empruntResponse.ouvrageIsbn( empruntExemplaireOuvrageIsbn( emprunt ) );
        empruntResponse.penaliteMontant( empruntPenaliteMontant( emprunt ) );
        empruntResponse.penalitePayee( empruntPenalitePayee( emprunt ) );
        empruntResponse.bibliothecaireId( empruntBibliothecaireId( emprunt ) );
        empruntResponse.bibliothecaireNomComplet( empruntBibliothecaireNomComplet( emprunt ) );
        empruntResponse.id( emprunt.getId() );
        empruntResponse.dateEmprunt( emprunt.getDateEmprunt() );
        empruntResponse.dateRetourPrevu( emprunt.getDateRetourPrevu() );
        empruntResponse.dateRetourReel( emprunt.getDateRetourReel() );
        empruntResponse.statut( emprunt.getStatut() );
        empruntResponse.prolonge( emprunt.getProlonge() );

        empruntResponse.enRetard( emprunt.estEnRetard() );
        empruntResponse.nombreJoursRetard( emprunt.getNombreJoursRetard() );

        return empruntResponse.build();
    }

    private Long empruntEtudiantId(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Etudiant etudiant = emprunt.getEtudiant();
        if ( etudiant == null ) {
            return null;
        }
        Long id = etudiant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String empruntEtudiantNomComplet(Emprunt emprunt) {
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

    private String empruntEtudiantEmail(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Etudiant etudiant = emprunt.getEtudiant();
        if ( etudiant == null ) {
            return null;
        }
        String email = etudiant.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private Long empruntExemplaireId(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Exemplaire exemplaire = emprunt.getExemplaire();
        if ( exemplaire == null ) {
            return null;
        }
        Long id = exemplaire.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String empruntExemplaireCodeBarres(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Exemplaire exemplaire = emprunt.getExemplaire();
        if ( exemplaire == null ) {
            return null;
        }
        String codeBarres = exemplaire.getCodeBarres();
        if ( codeBarres == null ) {
            return null;
        }
        return codeBarres;
    }

    private Long empruntExemplaireOuvrageId(Emprunt emprunt) {
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
        Long id = ouvrage.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String empruntExemplaireOuvrageTitre(Emprunt emprunt) {
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

    private String empruntExemplaireOuvrageAuteur(Emprunt emprunt) {
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
        String auteur = ouvrage.getAuteur();
        if ( auteur == null ) {
            return null;
        }
        return auteur;
    }

    private String empruntExemplaireOuvrageIsbn(Emprunt emprunt) {
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
        String isbn = ouvrage.getIsbn();
        if ( isbn == null ) {
            return null;
        }
        return isbn;
    }

    private BigDecimal empruntPenaliteMontant(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Penalite penalite = emprunt.getPenalite();
        if ( penalite == null ) {
            return null;
        }
        BigDecimal montant = penalite.getMontant();
        if ( montant == null ) {
            return null;
        }
        return montant;
    }

    private Boolean empruntPenalitePayee(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Penalite penalite = emprunt.getPenalite();
        if ( penalite == null ) {
            return null;
        }
        Boolean payee = penalite.getPayee();
        if ( payee == null ) {
            return null;
        }
        return payee;
    }

    private Long empruntBibliothecaireId(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Bibliothecaire bibliothecaire = emprunt.getBibliothecaire();
        if ( bibliothecaire == null ) {
            return null;
        }
        Long id = bibliothecaire.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String empruntBibliothecaireNomComplet(Emprunt emprunt) {
        if ( emprunt == null ) {
            return null;
        }
        Bibliothecaire bibliothecaire = emprunt.getBibliothecaire();
        if ( bibliothecaire == null ) {
            return null;
        }
        String nomComplet = bibliothecaire.getNomComplet();
        if ( nomComplet == null ) {
            return null;
        }
        return nomComplet;
    }
}
