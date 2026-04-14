package com.subsys.subscription;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.domain.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubscriptionDomainTest {

    @Test
    void shouldStartPendingPayment() {
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");

        assertEquals(SubscriptionStatus.PENDING_PAYMENT, subscription.getStatus());
        assertFalse(subscription.getCurrentPeriodStart() != null);
    }

    @Test
    void shouldActivateAfterInitialSuccess() {
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");

        subscription.activateInitialPayment(30);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertNotNull(subscription.getCurrentPeriodStart());
        assertNotNull(subscription.getCurrentPeriodEnd());
        assertNotNull(subscription.getNextRenewalDate());
    }

    @Test
    void shouldCancelOnInitialFailure() {
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_fail");

        subscription.cancelInitialPaymentFailure();

        assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
        assertFalse(subscription.isAutoRenew());
    }

    @Test
    void shouldMoveToPastDueOnRenewalFailure() {
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");
        subscription.activateInitialPayment(30);

        subscription.markRenewalFailed();

        assertEquals(SubscriptionStatus.PAST_DUE, subscription.getStatus());
        assertFalse(subscription.isAutoRenew());
    }

    @Test
    void shouldExtendFromCurrentPeriodEnd() {
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");
        subscription.activateInitialPayment(30);
        Instant firstEnd = subscription.getCurrentPeriodEnd();

        subscription.extendAfterRenewalSuccess(30);

        assertEquals(firstEnd.plusSeconds(30L * 24 * 60 * 60), subscription.getCurrentPeriodEnd());
    }
}
