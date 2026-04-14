package com.subsys.subscription.domain;

import com.subsys.contracts.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Subscription {
    private static final String INITIAL_PAYMENT_PENDING = "INITIAL_PAYMENT_PENDING";

    private UUID id;
    private UUID userId;
    private Integer planId;
    private SubscriptionStatus status;
    private boolean autoRenew;
    private String paymentMethodToken;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    private Instant nextRenewalDate;
    private UUID initialPaymentRequestId;
    private long version;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;
    private String statusReason;

    public static Subscription pendingPayment(UUID userId, Integer planId, String paymentMethodToken) {
        return Subscription.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .planId(planId)
                .status(SubscriptionStatus.PENDING_PAYMENT)
                .autoRenew(true)
                .paymentMethodToken(paymentMethodToken)
                .initialPaymentRequestId(UUID.randomUUID())
                .statusReason(INITIAL_PAYMENT_PENDING)
                .build();
    }

    public SubscriptionStatus activateInitialPayment(int billingPeriodDays) {
        if (status != SubscriptionStatus.PENDING_PAYMENT) {
            return status;
        }
        SubscriptionStatus previous = status;
        Instant now = Instant.now();
        status = SubscriptionStatus.ACTIVE;
        currentPeriodStart = now;
        currentPeriodEnd = now.plus(billingPeriodDays, ChronoUnit.DAYS);
        nextRenewalDate = currentPeriodEnd;
        statusReason = "INITIAL_PAYMENT_SUCCEEDED";
        return previous;
    }

    public SubscriptionStatus cancelInitialPaymentFailure() {
        if (status != SubscriptionStatus.PENDING_PAYMENT) {
            return status;
        }
        SubscriptionStatus previous = status;
        status = SubscriptionStatus.CANCELLED;
        autoRenew = false;
        cancelledAt = Instant.now();
        statusReason = "INITIAL_PAYMENT_FAILED";
        return previous;
    }

    public SubscriptionStatus cancelByUser() {
        if (status != SubscriptionStatus.ACTIVE && status != SubscriptionStatus.PAST_DUE) {
            throw new IllegalStateException("Subscription cannot be cancelled from status " + status);
        }
        SubscriptionStatus previous = status;
        status = SubscriptionStatus.CANCELLED;
        autoRenew = false;
        cancelledAt = Instant.now();
        statusReason = "USER_CANCELLED";
        return previous;
    }

    public SubscriptionStatus extendAfterRenewalSuccess(int billingPeriodDays) {
        if (status != SubscriptionStatus.ACTIVE) {
            return status;
        }
        SubscriptionStatus previous = status;
        Instant base = currentPeriodEnd == null ? Instant.now() : currentPeriodEnd;
        currentPeriodEnd = base.plus(billingPeriodDays, ChronoUnit.DAYS);
        nextRenewalDate = currentPeriodEnd;
        statusReason = "RENEWAL_SUCCEEDED";
        return previous;
    }

    public SubscriptionStatus markRenewalFailed() {
        if (status != SubscriptionStatus.ACTIVE) {
            return status;
        }
        SubscriptionStatus previous = status;
        status = SubscriptionStatus.PAST_DUE;
        autoRenew = false;
        statusReason = "RENEWAL_FAILED";
        return previous;
    }

    public boolean isRenewable(Instant now) {
        return status == SubscriptionStatus.ACTIVE && autoRenew && nextRenewalDate != null && !nextRenewalDate.isAfter(now);
    }
}
