package com.subsys.notification.adapter.out.persistence;

import com.subsys.notification.adapter.out.persistence.mapper.NotificationDeliveryPersistenceMapper;
import com.subsys.notification.adapter.out.persistence.repository.NotificationDeliveryJpaRepository;
import com.subsys.notification.application.port.out.write.NotificationDeliveryWriteOutPort;
import com.subsys.notification.domain.NotificationDelivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDeliveryPersistenceAdapter implements NotificationDeliveryWriteOutPort {
    private final NotificationDeliveryJpaRepository notificationDeliveryJpaRepository;
    private final NotificationDeliveryPersistenceMapper notificationDeliveryPersistenceMapper;

    @Override
    public NotificationDelivery save(NotificationDelivery notificationDelivery) {
        return notificationDeliveryPersistenceMapper.toDomain(
                notificationDeliveryJpaRepository.save(notificationDeliveryPersistenceMapper.toEntity(notificationDelivery))
        );
    }
}
