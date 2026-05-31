package com.unz.bibliotheque.security;

import com.unz.bibliotheque.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service d'authentification Spring Security.
 *
 * Implémente UserDetailsService pour charger les utilisateurs depuis
 * la base de données lors de la vérification des credentials.
 *
 * Spring Security appelle loadUserByUsername() automatiquement lors
 * de la tentative de connexion (POST /auth/login).
 *
 * L'email sert d'identifiant de connexion (username).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    /** Repository pour accéder aux utilisateurs en base de données */
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Charge un utilisateur par son email pour Spring Security.
     *
     * Retourne un objet UserDetails contenant :
     *   - l'email (username)
     *   - le mot de passe haché BCrypt
     *   - le rôle (ROLE_ETUDIANT, ROLE_BIBLIOTHECAIRE, ROLE_ADMINISTRATEUR)
     *
     * @param email adresse email de l'utilisateur (sert d'identifiant)
     * @return UserDetails avec les informations d'authentification
     * @throws UsernameNotFoundException si aucun utilisateur n'a cet email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Chargement de l'utilisateur par email : {}", email);

        var utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.warn("Utilisateur non trouvé pour l'email : {}", email);
                return new UsernameNotFoundException("Aucun compte trouvé pour : " + email);
            });

        // Vérification que le compte est actif
        if (!utilisateur.getActif()) {
            log.warn("Tentative de connexion sur un compte désactivé : {}", email);
            throw new UsernameNotFoundException("Ce compte est désactivé. Contactez l'administrateur.");
        }

        // Construction des autorités Spring Security (ROLE_ETUDIANT, etc.)
        String role = "ROLE_" + utilisateur.getRole().name();

        return new User(
            utilisateur.getEmail(),
            utilisateur.getMotDePasse(),
            List.of(new SimpleGrantedAuthority(role))
        );
    }
}
