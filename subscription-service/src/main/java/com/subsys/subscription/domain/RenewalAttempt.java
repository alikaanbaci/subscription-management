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
public class RenewalAttempt {
    private static final String REQUESTED = "REQUESTED";
    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";

    private UUID id;
    private UUID subscriptionId;
    private String billingPeriodKey;
    private UUID paymentRequestId;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public static RenewalAttempt requested(UUID subscriptionId, String billingPeriodKey, UUID paymentRequestId) {
        return RenewalAttempt.builder()
                .id(UUID.randomUUID())
                .subscriptionId(subscriptionId)
                .billingPeriodKey(billingPeriodKey)
                .paymentRequestId(paymentRequestId)
                .status(REQUESTED)
                .build();
    }

    public static RenewalAttempt completed(UUID subscriptionId, String billingPeriodKey, UUID paymentRequestId) {
        return RenewalAttempt.builder()
                .id(UUID.randomUUID())
                .subscriptionId(subscriptionId)
                .billingPeriodKey(billingPeriodKey)
                .paymentRequestId(paymentRequestId)
                .status(COMPLETED)
                .build();
    }

    public void markCompleted() {
        status = COMPLETED;
    }

    public void markFailed() {
        status = FAILED;
    }
}
