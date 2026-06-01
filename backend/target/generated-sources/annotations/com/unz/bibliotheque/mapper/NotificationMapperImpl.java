package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.NotificationResponse;
import com.unz.bibliotheque.model.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T20:56:53+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.19 (Eclipse Adoptium)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.id( notification.getId() );
        notificationResponse.type( notification.getType() );
        notificationResponse.sujet( notification.getSujet() );
        notificationResponse.message( notification.getMessage() );
        notificationResponse.lu( notification.getLu() );
        notificationResponse.dateEnvoi( notification.getDateEnvoi() );

        return notificationResponse.build();
    }
}
