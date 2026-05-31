package com.unz.bibliotheque.pattern.factory;

import com.unz.bibliotheque.model.Administrateur;
import com.unz.bibliotheque.model.Bibliothecaire;
import com.unz.bibliotheque.model.Etudiant;
import com.unz.bibliotheque.model.Utilisateur;
import com.unz.bibliotheque.model.enums.Role;

/**
 * Factory Pattern — Création d'utilisateurs selon leur rôle.
 *
 * Design Pattern : Factory Method
 * Justification : Centralise la création des 3 types d'utilisateurs (Etudiant,
 * Bibliothecaire, Administrateur) sans exposer la logique d'instanciation.
 * Respecte le principe OCP (Open/Closed) de SOLID.
 */
public class UtilisateurFactory {

    /**
     * Crée un utilisateur selon son rôle.
     *
     * @param role    rôle de l'utilisateur (ETUDIANT, BIBLIOTHECAIRE, ADMINISTRATEUR)
     * @param nom     nom de famille
     * @param prenom  prénom
     * @param email   adresse email
     * @return instance concrète de Utilisateur
     * @throws IllegalArgumentException si le rôle est inconnu
     */
    public static Utilisateur creerUtilisateur(Role role, String nom, String prenom, String email) {
        return switch (role) {
            case ETUDIANT -> {
                Etudiant e = new Etudiant();
                e.setNom(nom);
                e.setPrenom(prenom);
                e.setEmail(email);
                e.setRole(Role.ETUDIANT);
                e.setActif(true);
                yield e;
            }
            case BIBLIOTHECAIRE -> {
                Bibliothecaire b = new Bibliothecaire();
                b.setNom(nom);
                b.setPrenom(prenom);
                b.setEmail(email);
                b.setRole(Role.BIBLIOTHECAIRE);
                b.setActif(true);
                yield b;
            }
            case ADMINISTRATEUR -> {
                Administrateur a = new Administrateur();
                a.setNom(nom);
                a.setPrenom(prenom);
                a.setEmail(email);
                a.setRole(Role.ADMINISTRATEUR);
                a.setActif(true);
                yield a;
            }
        };
    }
}
