package com.subsys.notification.adapter.out.persistence.mapper;

import com.subsys.notification.adapter.out.persistence.entity.NotificationDeliveryEntity;
import com.subsys.notification.domain.NotificationDelivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationDeliveryPersistenceMapper {
    NotificationDelivery toDomain(NotificationDeliveryEntity entity);

    NotificationDeliveryEntity toEntity(NotificationDelivery domain);
}
