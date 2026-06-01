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
    date = "2026-05-31T20:56:52+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
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
        utilisateurResponse.id( utilisateur.getId() );
        utilisateurResponse.prenom( utilisateur.getPrenom() );
        utilisateurResponse.nom( utilisateur.getNom() );
        utilisateurResponse.nomComplet( utilisateur.getNomComplet() );
        utilisateurResponse.email( utilisateur.getEmail() );
        utilisateurResponse.role( utilisateur.getRole() );
        utilisateurResponse.actif( utilisateur.getActif() );
        utilisateurResponse.dateCreation( utilisateur.getDateCreation() );
        utilisateurResponse.dateModification( utilisateur.getDateModification() );

        return utilisateurResponse.build();
    }

    @Override
    public Etudiant toEtudiant(UtilisateurRequest request) {
        if ( request == null ) {
            return null;
        }

        Etudiant etudiant = new Etudiant();

        etudiant.setPrenom( request.getPrenom() );
        etudiant.setNom( request.getNom() );
        etudiant.setEmail( request.getEmail() );
        etudiant.setMotDePasse( request.getMotDePasse() );
        etudiant.setMatricule( request.getMatricule() );
        etudiant.setFiliere( request.getFiliere() );
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

        bibliothecaire.setPrenom( request.getPrenom() );
        bibliothecaire.setNom( request.getNom() );
        bibliothecaire.setEmail( request.getEmail() );
        bibliothecaire.setMotDePasse( request.getMotDePasse() );
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

        administrateur.setPrenom( request.getPrenom() );
        administrateur.setNom( request.getNom() );
        administrateur.setEmail( request.getEmail() );
        administrateur.setMotDePasse( request.getMotDePasse() );
        administrateur.setDepartement( request.getDepartement() );

        administrateur.setActif( true );
        administrateur.setRole( com.unz.bibliotheque.model.enums.Role.ADMINISTRATEUR );

        return administrateur;
    }
}
