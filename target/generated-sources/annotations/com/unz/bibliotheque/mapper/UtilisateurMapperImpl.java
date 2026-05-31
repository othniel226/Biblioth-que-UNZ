package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.request.UtilisateurRequest;
import com.unz.bibliotheque.dto.response.UtilisateurResponse;
import com.unz.bibliotheque.model.Administrateur;
import com.unz.bibliotheque.model.Bibliothecaire;
import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Utilisateur;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-30T20:08:20+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UtilisateurMapperImpl implements UtilisateurMapper {

    @Override
    public UtilisateurResponse toResponse(Utilisateur utilisateur) {
        if ( utilisateur == null ) {
            return null;
        }

        UtilisateurResponse.UtilisateurResponseBuilder utilisateurResponse = UtilisateurResponse.builder();

        utilisateurResponse.matricule( extractMatricule( utilisateur ) );
        utilisateurResponse.filiere( extractFiliere( utilisateur ) );
        utilisateurResponse.niveau( extractNiveau( utilisateur ) );
        utilisateurResponse.badgeNumero( extractBadgeNumero( utilisateur ) );
        utilisateurResponse.service( extractService( utilisateur ) );
        utilisateurResponse.departement( extractDepartement( utilisateur ) );
        utilisateurResponse.actif( utilisateur.getActif() );
        utilisateurResponse.dateCreation( utilisateur.getDateCreation() );
        utilisateurResponse.dateModification( utilisateur.getDateModification() );
        utilisateurResponse.email( utilisateur.getEmail() );
        utilisateurResponse.id( utilisateur.getId() );
        utilisateurResponse.nom( utilisateur.getNom() );
        utilisateurResponse.nomComplet( utilisateur.getNomComplet() );
        utilisateurResponse.prenom( utilisateur.getPrenom() );
        utilisateurResponse.role( utilisateur.getRole() );

        return utilisateurResponse.build();
    }

    @Override
    public Etudiant toEtudiant(UtilisateurRequest request) {
        if ( request == null ) {
            return null;
        }

        Etudiant etudiant = new Etudiant();

        etudiant.setEmail( request.getEmail() );
        etudiant.setMotDePasse( request.getMotDePasse() );
        etudiant.setNom( request.getNom() );
        etudiant.setPrenom( request.getPrenom() );
        etudiant.setFiliere( request.getFiliere() );
        etudiant.setMatricule( request.getMatricule() );
        etudiant.setNiveau( request.getNiveau() );

        etudiant.setActif( true );
        etudiant.setRole( com.unz.bibliotheque.model.enums.Role.ETUDIANT );

        return etudiant;
    }

    @Override
    public Bibliothecaire toBibliothecaire(UtilisateurRequest request) {
        if ( request == null ) {
            return null;
        }

        Bibliothecaire bibliothecaire = new Bibliothecaire();

        bibliothecaire.setEmail( request.getEmail() );
        bibliothecaire.setMotDePasse( request.getMotDePasse() );
        bibliothecaire.setNom( request.getNom() );
        bibliothecaire.setPrenom( request.getPrenom() );
        bibliothecaire.setBadgeNumero( request.getBadgeNumero() );
        bibliothecaire.setService( request.getService() );

        bibliothecaire.setActif( true );
        bibliothecaire.setRole( com.unz.bibliotheque.model.enums.Role.BIBLIOTHECAIRE );

        return bibliothecaire;
    }

    @Override
    public Administrateur toAdministrateur(UtilisateurRequest request) {
        if ( request == null ) {
            return null;
        }

        Administrateur administrateur = new Administrateur();

        administrateur.setEmail( request.getEmail() );
        administrateur.setMotDePasse( request.getMotDePasse() );
        administrateur.setNom( request.getNom() );
        administrateur.setPrenom( request.getPrenom() );
        administrateur.setDepartement( request.getDepartement() );

        administrateur.setActif( true );
        administrateur.setRole( com.unz.bibliotheque.model.enums.Role.ADMINISTRATEUR );

        return administrateur;
    }
}
