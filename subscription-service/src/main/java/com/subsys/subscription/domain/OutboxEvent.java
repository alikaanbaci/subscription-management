package com.subsys.subscription.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OutboxEvent {
    private static final String PENDING = "PENDING";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String SUBSCRIPTION_AGGREGATE = "Subscription";
    private static final String PAYMENT_REQUESTED_EVENT = "PaymentRequested";
    private static final String SUBSCRIPTION_STATUS_CHANGED_EVENT = "SubscriptionStatusChanged";

    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String eventType;
    private String eventKey;
    private String payload;
    private String status;
    private Instant publishedAt;
    private Instant claimedAt;
    private int retryCount;
    private Instant createdAt;

    public static OutboxEvent paymentRequested(UUID aggregateId, String eventKey, String payload) {
        return pending(aggregateId, PAYMENT_REQUESTED_EVENT, eventKey, payload);
    }

    public static OutboxEvent subscriptionStatusChanged(UUID aggregateId, String eventKey, String payload) {
        return pending(aggregateId, SUBSCRIPTION_STATUS_CHANGED_EVENT, eventKey, payload);
    }

    public void markPublished() {
        status = "PUBLISHED";
        publishedAt = Instant.now();
        claimedAt = null;
    }

    public void markInProgress(Instant claimedAt) {
        status = IN_PROGRESS;
        this.claimedAt = claimedAt;
    }

    public void markRetry() {
        status = PENDING;
        claimedAt = null;
        retryCount++;
    }

    private static OutboxEvent pending(UUID aggregateId, String eventType, String eventKey, String payload) {
        return OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateType(OutboxEvent.SUBSCRIPTION_AGGREGATE)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .eventKey(eventKey)
                .payload(payload)
                .status(PENDING)
                .retryCount(0)
                .build();
    }
}
