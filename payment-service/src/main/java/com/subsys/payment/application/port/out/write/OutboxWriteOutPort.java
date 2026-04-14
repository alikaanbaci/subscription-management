package com.subsys.payment.application.port.out.write;

import com.subsys.payment.domain.OutboxEvent;

import java.util.UUID;

public interface OutboxWriteOutPort {
    OutboxEvent save(OutboxEvent outboxEvent);

    void paymentCompleted(UUID aggregateId, String eventKey, Object payload);
}
