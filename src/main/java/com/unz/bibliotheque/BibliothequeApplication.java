package com.unz.bibliotheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale de l'application Bibliothèque UNZ.
 * 
 * @SpringBootApplication active :
 *   - @Configuration (configuration Spring)
 *   - @EnableAutoConfiguration (configuration automatique)
 *   - @ComponentScan (scan des composants dans le package)
 * 
 * @EnableScheduling active les tâches planifiées (@Scheduled)
 * utilisées pour les rappels de retour et la vérification des retards.
 * 
 * @author Équipe Projet Génie Logiciel L3 — UNZ
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class BibliothequeApplication {

    /**
     * Point d'entrée de l'application Spring Boot.
     * Lance le serveur Tomcat embarqué sur le port configuré dans application.yml.
     *
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SpringApplication.run(BibliothequeApplication.class, args);
    }
}
