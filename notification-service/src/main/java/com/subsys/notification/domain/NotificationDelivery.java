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
public class NotificationDelivery {
    private static final String CHANNEL_EMAIL = "EMAIL";
    private static final String STATUS_DELIVERED = "DELIVERED";

    private UUID id;
    private UUID eventId;
    private UUID subscriptionId;
    private UUID userId;
    private String notificationType;
    private String channel;
    private String status;
    private String failureReason;
    private int attemptCount;
    private Instant createdAt;
    private Instant updatedAt;

    public void markFailed(String reason) {
        status = "FAILED";
        failureReason = reason;
        attemptCount++;
    }

    public static NotificationDelivery paymentResult(PaymentCompletedEvent event) {
        return delivered(event.eventId(), event.subscriptionId(), "PAYMENT_" + event.result().name());
    }

    public static NotificationDelivery subscriptionStatus(SubscriptionStatusChangedEvent event) {
        return delivered(event.getEventId(), event.getSubscriptionId(), "SUBSCRIPTION_" + event.getNewStatus().name());
    }

    private static NotificationDelivery delivered(UUID eventId, UUID subscriptionId, String notificationType) {
        return NotificationDelivery.builder()
                .id(UUID.randomUUID())
                .eventId(eventId)
                .subscriptionId(subscriptionId)
                .userId(null)
                .notificationType(notificationType)
                .channel(CHANNEL_EMAIL)
                .status(STATUS_DELIVERED)
                .attemptCount(1)
                .build();
    }
}
