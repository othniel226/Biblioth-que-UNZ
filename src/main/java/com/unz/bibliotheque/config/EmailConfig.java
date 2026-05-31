package com.unz.bibliotheque.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

/**
 * Configuration du serveur SMTP pour l'envoi d'emails.
 *
 * Utilise Gmail SMTP avec authentification App Password.
 * Les credentials sont configurés dans application.yml et
 * peuvent être surchargés via des variables d'environnement.
 *
 * Pour utiliser Gmail :
 * 1. Activer la validation en 2 étapes sur le compte Gmail
 * 2. Générer un App Password (16 caractères)
 * 3. Configurer MAIL_USERNAME et MAIL_PASSWORD dans .env
 */
@Configuration
public class EmailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:bibliotheque.unz@gmail.com}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    /**
     * Configure le bean JavaMailSender pour l'envoi d'emails.
     *
     * Paramètres SMTP :
     *   - Hôte : smtp.gmail.com
     *   - Port : 587 (TLS)
     *   - Authentification : App Password Gmail
     *   - STARTTLS : activé (chiffrement TLS)
     *
     * @return instance configurée de JavaMailSender
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "false"); // Mettre true pour déboguer

        return mailSender;
    }
}
