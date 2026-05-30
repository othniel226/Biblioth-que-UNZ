package com.unz.bibliotheque.mapper;

import com.unz.bibliotheque.dto.response.NotificationResponse;
import com.unz.bibliotheque.model.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-30T20:08:18+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.dateEnvoi( notification.getDateEnvoi() );
        notificationResponse.id( notification.getId() );
        notificationResponse.lu( notification.getLu() );
        notificationResponse.message( notification.getMessage() );
        notificationResponse.sujet( notification.getSujet() );
        notificationResponse.type( notification.getType() );

        return notificationResponse.build();
    }
}
