package com.subsys.notification.application.port.out.read;

import java.util.UUID;

public interface NotificationProcessedEventReadOutPort {
    boolean existsById(UUID eventId);
}
