package com.subsys.subscription.application.service;

import com.subsys.contracts.SubscriptionStatusChangedEvent;
import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.port.out.write.OutboxWriteOutPort;
import com.subsys.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionStatusEventService {
    private final OutboxWriteOutPort outboxWriteOutPort;

    public void enqueueStatusChanged(Subscription subscription, SubscriptionStatus oldStatus, String reason,
            UUID correlationId) {
        if (oldStatus == subscription.getStatus()) {
            return;
        }

        SubscriptionStatusChangedEvent event = SubscriptionStatusChangedEvent.statusChanged(
                correlationId,
                subscription.getId(),
                oldStatus,
                subscription.getStatus(),
                reason);

        outboxWriteOutPort.subscriptionStatusChanged(subscription.getId(), UUID.randomUUID().toString(), event);
    }
}
