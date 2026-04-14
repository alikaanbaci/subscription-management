package com.subsys.notification.domain;

import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.SubscriptionStatusChangedEvent;
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
public class ProcessedEvent {
    private static final String PAYMENT_COMPLETED_NOTIFICATION_CONSUMER = "payment-completed-notification";
    private static final String SUBSCRIPTION_STATUS_NOTIFICATION_CONSUMER = "subscription-status-notification";

    private UUID eventId;
    private String consumerName;
    private Instant processedAt;
    private UUID correlationId;

    public static ProcessedEvent paymentCompletedNotification(PaymentCompletedEvent event) {
        return ProcessedEvent.builder()
                .eventId(event.eventId())
                .consumerName(PAYMENT_COMPLETED_NOTIFICATION_CONSUMER)
                .processedAt(Instant.now())
                .correlationId(event.correlationId())
                .build();
    }

    public static ProcessedEvent subscriptionStatusNotification(SubscriptionStatusChangedEvent event) {
        return ProcessedEvent.builder()
                .eventId(event.getEventId())
                .consumerName(SUBSCRIPTION_STATUS_NOTIFICATION_CONSUMER)
                .processedAt(Instant.now())
                .correlationId(event.getCorrelationId())
                .build();
    }
}
