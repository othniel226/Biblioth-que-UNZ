package com.unz.bibliotheque.repository;

import com.unz.bibliotheque.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository JPA pour l'entité Notification.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retourne les N dernières notifications d'un utilisateur.
     * Triées par date d'envoi décroissante (plus récentes en premier).
     *
     * @param userId   identifiant de l'utilisateur
     * @param pageable pagination (généralement top 5)
     * @return liste des dernières notifications
     */
    @Query("""
        SELECT n FROM Notification n
        WHERE n.destinataire.id = :userId
        ORDER BY n.dateEnvoi DESC
        """)
    List<Notification> findTopByDestinataireId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Compte les notifications non lues d'un utilisateur.
     * Utilisé pour afficher le badge de notification dans l'interface.
     *
     * @param destinataireId identifiant du destinataire
     * @return nombre de notifications non lues
     */
    long countByDestinataireIdAndLuFalse(Long destinataireId);

    /**
     * Compte les notifications non lues.
     * @return nombre de notifications non lues
     */
    long countByLuFalse();
}
