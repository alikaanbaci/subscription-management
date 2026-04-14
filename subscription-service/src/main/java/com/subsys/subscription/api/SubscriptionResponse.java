package com.subsys.subscription.api;

import com.subsys.contracts.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionResponse(
        UUID subscriptionId,
        UUID userId,
        Integer planId,
        SubscriptionStatus status,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        Instant nextRenewalDate,
        boolean autoRenew,
        String message
) {
}
