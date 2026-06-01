package com.unz.bibliotheque.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtre Spring Security pour l'authentification JWT.
 *
 * Intercepte chaque requête HTTP et vérifie la présence d'un token JWT
 * dans l'en-tête Authorization (format : "Bearer {token}").
 *
 * Si le token est valide, l'utilisateur est authentifié dans le SecurityContext
 * de Spring Security, permettant l'accès aux ressources protégées.
 *
 * Ce filtre s'exécute UNE SEULE FOIS par requête (hérite de OncePerRequestFilter).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    /** Utilitaire JWT pour la validation et l'extraction des claims */
    private final JwtUtils jwtUtils;

    /** Service de chargement des utilisateurs depuis la base de données */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Logique principale du filtre JWT.
     *
     * Étapes :
     * 1. Extraire l'en-tête Authorization
     * 2. Vérifier qu'il commence par "Bearer "
     * 3. Extraire le token JWT
     * 4. Extraire l'email depuis le token
     * 5. Charger l'utilisateur depuis la BDD
     * 6. Valider le token
     * 7. Authentifier l'utilisateur dans le SecurityContext
     *
     * @param request     requête HTTP entrante
     * @param response    réponse HTTP sortante
     * @param filterChain chaîne de filtres suivante
     */
    /**
 * Indique à Spring de ne pas appliquer ce filtre JWT
 * sur les URLs Thymeleaf (formulaire de connexion).
 * Le filtre JWT est uniquement pour les API REST (/api/**).
 */
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/auth/")
        || path.startsWith("/css/")
        || path.startsWith("/js/")
        || path.startsWith("/images/")
        || path.equals("/dashboard")
        || path.startsWith("/etudiant/")
        || path.startsWith("/bibliothecaire/")
        || path.startsWith("/admin/")
        || path.startsWith("/catalogue");
}
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraire l'en-tête Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si pas de token Bearer, continuer sans authentification
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (enlever "Bearer ")
        final String token = authHeader.substring(7);
        String email = null;

        try {
            // 4. Extraire l'email depuis le token
            email = jwtUtils.getEmailFromToken(token);
        } catch (Exception e) {
            log.warn("Impossible d'extraire l'email du token JWT : {}", e.getMessage());
        }

        // 5. Si email extrait et utilisateur non encore authentifié
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 6. Valider le token
            if (jwtUtils.isTokenValid(token, userDetails)) {
                // 7. Créer l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Enregistrer dans le SecurityContext (utilisateur authentifié)
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Utilisateur authentifié via JWT : {}", email);
            }
        }

        // Passer au filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }
}
