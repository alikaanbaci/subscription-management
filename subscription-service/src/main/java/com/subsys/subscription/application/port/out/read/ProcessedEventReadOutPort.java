package com.subsys.subscription.application.port.out.read;

import java.util.UUID;

public interface ProcessedEventReadOutPort {
    boolean existsById(UUID eventId);
}
