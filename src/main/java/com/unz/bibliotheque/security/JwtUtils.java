package com.unz.bibliotheque.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Utilitaire de gestion des tokens JWT (JSON Web Token).
 *
 * Design Pattern : Singleton (Spring crée un seul bean @Component).
 *
 * Fonctions principales :
 *   - Générer un token JWT à partir des informations d'un utilisateur
 *   - Valider un token JWT (signature + expiration)
 *   - Extraire l'email de l'utilisateur depuis un token
 *
 * Le token JWT contient :
 *   - Subject : email de l'utilisateur
 *   - IssuedAt : date de création
 *   - Expiration : date d'expiration (24h par défaut)
 *   - Signature : HMAC-SHA256 avec la clé secrète
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * Clé secrète pour la signature HMAC-SHA256.
     * Configurée dans application.yml via ${app.jwt.secret}.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Durée de validité du token en millisecondes.
     * 86400000 ms = 24 heures (valeur par défaut).
     */
    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    /**
     * Génère un token JWT pour un utilisateur authentifié.
     *
     * @param userDetails détails de l'utilisateur connecté
     * @return token JWT signé (String)
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())        // email de l'utilisateur
            .setIssuedAt(new Date())                      // date de création
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // expiration
            .signWith(getSignKey(), SignatureAlgorithm.HS256) // signature HMAC-SHA256
            .compact();
    }

    /**
     * Génère un token JWT avec une date d'émission personnalisée (pour les tests).
     *
     * @param userDetails détails de l'utilisateur connecté
     * @param issuedAt    date d'émission personnalisée
     * @return token JWT signé
     */
    public String generateToken(UserDetails userDetails, Date issuedAt) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(issuedAt)
            .setExpiration(new Date(issuedAt.getTime() + jwtExpirationMs))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Extrait l'email (subject) depuis un token JWT.
     *
     * @param token token JWT à analyser
     * @return email de l'utilisateur contenu dans le token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * Valide un token JWT.
     * Vérifie la signature et l'expiration.
     *
     * @param token       token JWT à valider
     * @param userDetails informations de l'utilisateur attendu
     * @return true si le token est valide et correspond à l'utilisateur
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = getEmailFromToken(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token JWT invalide : {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un token JWT est expiré.
     *
     * @param token token JWT à vérifier
     * @return true si le token est expiré
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return expiration.before(new Date());
    }

    /**
     * Construit la clé de signature à partir du secret configuré.
     * Décode le secret en Base64 et crée une clé HMAC-SHA256.
     *
     * @return clé de signature JWT
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
