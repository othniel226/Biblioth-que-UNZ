package com.unz.bibliotheque.dto.response;

import com.unz.bibliotheque.model.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour afficher une notification in-app.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private TypeNotification type;
    private String sujet;
    private String message;
    private Boolean lu;
    private LocalDateTime dateEnvoi;
}
