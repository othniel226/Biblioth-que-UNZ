package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.NotificationResponse;
import com.unz.bibliotheque.model.Notification;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct pour les notifications.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}
