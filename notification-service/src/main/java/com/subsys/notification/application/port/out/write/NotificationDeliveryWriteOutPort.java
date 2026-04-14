package com.subsys.notification.application.port.out.write;

import com.subsys.notification.domain.NotificationDelivery;

public interface NotificationDeliveryWriteOutPort {
    NotificationDelivery save(NotificationDelivery notificationDelivery);
}
