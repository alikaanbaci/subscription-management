package com.subsys.subscription;

import com.subsys.subscription.application.port.out.read.SubscriptionReadOutPort;
import com.subsys.subscription.application.port.out.write.RenewalAttemptWriteOutPort;
import com.subsys.subscription.application.service.BillingPeriodService;
import com.subsys.subscription.domain.PaymentRequestDispatch;
import com.subsys.subscription.application.service.PaymentRequestEventService;
import com.subsys.subscription.application.service.ScheduleRenewalsService;
import com.subsys.subscription.application.service.SubscriptionPolicyService;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.Subscription;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduleRenewalsServiceTest {

    @Test
    void shouldIgnoreDuplicateRenewalClaim() {
        SubscriptionReadOutPort subscriptionReadOutPort = mock(SubscriptionReadOutPort.class);
        RenewalAttemptWriteOutPort renewalAttemptWriteOutPort = mock(RenewalAttemptWriteOutPort.class);
        SubscriptionPolicyService subscriptionPolicyService = mock(SubscriptionPolicyService.class);
        BillingPeriodService billingPeriodService = new BillingPeriodService();
        PaymentRequestEventService paymentRequestEventService = mock(PaymentRequestEventService.class);
        ScheduleRenewalsService service = new ScheduleRenewalsService(
                subscriptionReadOutPort,
                renewalAttemptWriteOutPort,
                subscriptionPolicyService,
                billingPeriodService,
                paymentRequestEventService,
                transactionManager()
        );

        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");
        subscription.activateInitialPayment(30);
        subscription.setCurrentPeriodStart(Instant.now().minusSeconds(86400));
        subscription.setCurrentPeriodEnd(Instant.now());
        subscription.setNextRenewalDate(Instant.now().minusSeconds(60));
        Plan plan = Plan.builder()
                .id(1)
                .code("basic")
                .name("Basic")
                .price(new BigDecimal("9.99"))
                .currency("USD")
                .billingPeriodDays(30)
                .active(true)
                .build();

        when(subscriptionReadOutPort.findDueRenewals(any())).thenReturn(List.of(subscription));
        when(subscriptionPolicyService.getActivePlan(1)).thenReturn(plan);
        when(paymentRequestEventService.enqueueRenewalPaymentRequested(any(), any(), any()))
                .thenReturn(new PaymentRequestDispatch(UUID.randomUUID(), UUID.randomUUID()));
        doThrow(new DataIntegrityViolationException("dup")).when(renewalAttemptWriteOutPort).save(any());

        service.scheduleRenewals();

        verify(paymentRequestEventService, never()).enqueueRenewalPaymentRequested(any(), any(), any());
        verify(renewalAttemptWriteOutPort, times(1)).save(any());
    }

    @Test
    void shouldContinueProcessingWhenOneRenewalFails() {
        SubscriptionReadOutPort subscriptionReadOutPort = mock(SubscriptionReadOutPort.class);
        RenewalAttemptWriteOutPort renewalAttemptWriteOutPort = mock(RenewalAttemptWriteOutPort.class);
        SubscriptionPolicyService subscriptionPolicyService = mock(SubscriptionPolicyService.class);
        BillingPeriodService billingPeriodService = new BillingPeriodService();
        PaymentRequestEventService paymentRequestEventService = mock(PaymentRequestEventService.class);
        ScheduleRenewalsService service = new ScheduleRenewalsService(
                subscriptionReadOutPort,
                renewalAttemptWriteOutPort,
                subscriptionPolicyService,
                billingPeriodService,
                paymentRequestEventService,
                transactionManager()
        );

        Subscription firstSubscription = renewableSubscription();
        Subscription secondSubscription = renewableSubscription();
        Plan plan = plan();

        when(subscriptionReadOutPort.findDueRenewals(any())).thenReturn(List.of(firstSubscription, secondSubscription));
        when(subscriptionPolicyService.getActivePlan(anyInt())).thenReturn(plan);
        when(renewalAttemptWriteOutPort.save(any()))
                .thenThrow(new IllegalStateException("unexpected payment request state"))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRequestEventService.enqueueRenewalPaymentRequested(any(), any(), any()))
                .thenReturn(new PaymentRequestDispatch(UUID.randomUUID(), UUID.randomUUID()));

        service.scheduleRenewals();

        verify(renewalAttemptWriteOutPort, times(2)).save(any());
        verify(paymentRequestEventService, times(1)).enqueueRenewalPaymentRequested(any(), any(), any());
    }

    private static PlatformTransactionManager transactionManager() {
        PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
        TransactionStatus transactionStatus = new SimpleTransactionStatus();
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        return transactionManager;
    }

    private static Subscription renewableSubscription() {
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");
        subscription.activateInitialPayment(30);
        subscription.setCurrentPeriodStart(Instant.now().minusSeconds(86400));
        subscription.setCurrentPeriodEnd(Instant.now());
        subscription.setNextRenewalDate(Instant.now().minusSeconds(60));
        return subscription;
    }

    private static Plan plan() {
        return Plan.builder()
                .id(1)
                .code("basic")
                .name("Basic")
                .price(new BigDecimal("9.99"))
                .currency("USD")
                .billingPeriodDays(30)
                .active(true)
                .build();
    }
}
