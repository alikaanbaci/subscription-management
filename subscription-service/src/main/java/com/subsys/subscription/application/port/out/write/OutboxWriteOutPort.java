package com.subsys.subscription.application.port.out.write;

import com.subsys.subscription.domain.OutboxEvent;

import java.util.UUID;

public interface OutboxWriteOutPort {
    OutboxEvent save(OutboxEvent outboxEvent);

    OutboxEvent paymentRequested(UUID aggregateId, String eventKey, Object payload);

    OutboxEvent subscriptionStatusChanged(UUID aggregateId, String eventKey, Object payload);
}
