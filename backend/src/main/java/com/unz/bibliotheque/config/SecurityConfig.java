package com.unz.bibliotheque.config;

import com.unz.bibliotheque.security.JwtAuthFilter;
import com.unz.bibliotheque.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration Spring Security de l'application Bibliothèque UNZ.
 *
 * Architecture de sécurité :
 *   - Authentification : formulaire Thymeleaf + JWT pour les API REST
 *   - Autorisation : RBAC (Role-Based Access Control) par URL
 *   - Hachage : BCrypt avec facteur de coût 10
 *   - Sessions : STATELESS pour les API, SESSION pour Thymeleaf
 *
 * Règles d'accès par rôle :
 *   - Public : /auth/**, /catalogue, /swagger-ui.html, /api-docs
 *   - ETUDIANT : /etudiant/**
 *   - BIBLIOTHECAIRE : /bibliothecaire/**, /etudiant/** (accès bibliothécaire)
 *   - ADMINISTRATEUR : /admin/**, tout le reste
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Active @PreAuthorize, @PostAuthorize dans les contrôleurs
@RequiredArgsConstructor
public class SecurityConfig {

    /** Filtre JWT personnalisé — injecté dans la chaîne de filtres */
    private final JwtAuthFilter jwtAuthFilter;

    /** Service de chargement des utilisateurs */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Configure l'encodeur de mots de passe BCrypt.
     * Coût 10 (recommandé pour la sécurité, ~100ms pour hacher).
     *
     * @return encodeur BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configure le fournisseur d'authentification.
     * Utilise la base de données pour vérifier les credentials.
     *
     * @return fournisseur d'authentification DAO
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expose l'AuthenticationManager (nécessaire pour AuthController).
     *
     * @param config configuration d'authentification Spring
     * @return gestionnaire d'authentification
     * @throws Exception si la configuration échoue
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure la chaîne de filtres de sécurité Spring.
     *
     * @param http constructeur de sécurité HTTP
     * @return chaîne de filtres configurée
     * @throws Exception si la configuration échoue
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF (non nécessaire avec JWT stateless)
            .csrf(csrf -> csrf.disable())

            // Règles d'autorisation par URL
            .authorizeHttpRequests(auth -> auth
                // Ressources publiques (pas d'authentification requise)
               .requestMatchers(
                       "/auth/**",
                       "/catalogue", "/catalogue/**",
                       "/api/catalogue/**",        
                       "/api/auth/**",             
                       "/swagger-ui/**",
                       "/swagger-ui.html",
                       "/api-docs/**",
                       "/h2-console/**",
                       "/css/**", "/js/**", "/images/**"
                 ).permitAll()

                // Espace étudiant
                .requestMatchers("/etudiant/**").hasAnyRole("ETUDIANT", "BIBLIOTHECAIRE", "ADMINISTRATEUR")

                // Espace bibliothécaire
                .requestMatchers("/bibliothecaire/**").hasAnyRole("BIBLIOTHECAIRE", "ADMINISTRATEUR")

                // Espace administration (accès total)
                .requestMatchers("/admin/**").hasRole("ADMINISTRATEUR")

                // Toute autre requête nécessite une authentification
                .anyRequest().authenticated()
            )

            // Configuration du formulaire de connexion Thymeleaf
            .formLogin(form -> form
                .loginPage("/auth/login")                // Page de connexion personnalisée
                .loginProcessingUrl("/auth/login")       // URL de traitement du formulaire
                .defaultSuccessUrl("/dashboard", true)   // Redirection après connexion réussie
                .failureUrl("/auth/login?error=true")    // Redirection en cas d'erreur
                .permitAll()
            )

            // Configuration de la déconnexion
            .logout(logout -> logout
                .logoutUrl("/auth/logout")               // URL de déconnexion
                .logoutSuccessUrl("/auth/login?logout=true") // Redirection après déconnexion
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )

            // Politique de session (STATELESS pour les API REST)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            // Injecter le filtre JWT avant le filtre d'authentification standard
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
