package com.subsys.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SubscriptionStatusChangedEvent {
        private final UUID eventId;
        private final UUID correlationId;
        private final UUID subscriptionId;
        private final SubscriptionStatus oldStatus;
        private final SubscriptionStatus newStatus;
        private final String reason;
        private final Instant occurredAt;

        public static SubscriptionStatusChangedEvent statusChanged(
                        UUID correlationId,
                        UUID subscriptionId,
                        SubscriptionStatus oldStatus,
                        SubscriptionStatus newStatus,
                        String reason) {
                return new SubscriptionStatusChangedEvent(
                                UUID.randomUUID(),
                                correlationId,
                                subscriptionId,
                                oldStatus,
                                newStatus,
                                reason,
                                Instant.now());
        }
}
