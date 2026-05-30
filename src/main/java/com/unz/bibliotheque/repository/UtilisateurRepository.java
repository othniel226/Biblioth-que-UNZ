package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Utilisateur;
import com.unz.bibliotheque.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository JPA pour l'entité Utilisateur.
 * Utilisé principalement pour l'authentification Spring Security.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par son adresse email.
     * Utilisé par UserDetailsServiceImpl pour charger l'utilisateur lors de la connexion.
     *
     * @param email adresse email de l'utilisateur
     * @return Optional contenant l'utilisateur trouvé, ou vide si inexistant
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Vérifie si un email est déjà utilisé dans le système.
     * Utilisé lors de la création de compte pour éviter les doublons.
     *
     * @param email adresse email à vérifier
     * @return true si l'email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Compte les utilisateurs par role.
     * @param role role de l utilisateur
     * @return nombre d utilisateurs ayant ce role
     */
    long countByRole(Role role);
}
