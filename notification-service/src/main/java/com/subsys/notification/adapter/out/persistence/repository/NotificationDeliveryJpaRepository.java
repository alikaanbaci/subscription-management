package com.subsys.notification.adapter.out.persistence.repository;

import com.subsys.notification.adapter.out.persistence.entity.NotificationDeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationDeliveryJpaRepository extends JpaRepository<NotificationDeliveryEntity, UUID> {
}
