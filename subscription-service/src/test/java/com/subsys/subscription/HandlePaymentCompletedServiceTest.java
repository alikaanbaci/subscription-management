package com.subsys.subscription;

import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.PaymentResult;
import com.subsys.contracts.PaymentType;
import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.port.out.read.ProcessedEventReadOutPort;
import com.subsys.subscription.application.port.out.read.RenewalAttemptReadOutPort;
import com.subsys.subscription.application.port.out.write.ProcessedEventWriteOutPort;
import com.subsys.subscription.application.port.out.write.RenewalAttemptWriteOutPort;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.application.service.HandlePaymentCompletedService;
import com.subsys.subscription.application.service.SubscriptionPolicyService;
import com.subsys.subscription.application.service.SubscriptionStatusEventService;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.RenewalAttempt;
import com.subsys.subscription.domain.Subscription;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HandlePaymentCompletedServiceTest {

    @Test
    void shouldIgnoreDuplicatePaymentCompletedEvent() {
        ProcessedEventReadOutPort processedEventReadOutPort = mock(ProcessedEventReadOutPort.class);
        HandlePaymentCompletedService service = new HandlePaymentCompletedService(
                mock(SubscriptionPolicyService.class),
                mock(SubscriptionWriteOutPort.class),
                mock(RenewalAttemptReadOutPort.class),
                mock(RenewalAttemptWriteOutPort.class),
                processedEventReadOutPort,
                mock(ProcessedEventWriteOutPort.class),
                mock(SubscriptionStatusEventService.class)
        );
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), PaymentType.INITIAL,
                PaymentResult.SUCCEEDED, "provider", null, Instant.now()
        );

        when(processedEventReadOutPort.existsById(event.eventId())).thenReturn(true);

        service.handlePaymentCompleted(event);

        verify(processedEventReadOutPort, times(1)).existsById(event.eventId());
    }

    @Test
    void shouldMoveInitialFailureToCancelled() {
        SubscriptionPolicyService subscriptionPolicyService = mock(SubscriptionPolicyService.class);
        SubscriptionWriteOutPort subscriptionWriteOutPort = mock(SubscriptionWriteOutPort.class);
        ProcessedEventReadOutPort processedEventReadOutPort = mock(ProcessedEventReadOutPort.class);
        ProcessedEventWriteOutPort processedEventWriteOutPort = mock(ProcessedEventWriteOutPort.class);
        SubscriptionStatusEventService subscriptionStatusEventService = mock(SubscriptionStatusEventService.class);

        HandlePaymentCompletedService service = new HandlePaymentCompletedService(
                subscriptionPolicyService,
                subscriptionWriteOutPort,
                mock(RenewalAttemptReadOutPort.class),
                mock(RenewalAttemptWriteOutPort.class),
                processedEventReadOutPort,
                processedEventWriteOutPort,
                subscriptionStatusEventService
        );

        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_fail");
        subscription.setId(subscriptionId);
        Plan plan = Plan.builder()
                .id(1)
                .code("basic")
                .name("Basic")
                .price(new BigDecimal("9.99"))
                .currency("USD")
                .billingPeriodDays(30)
                .active(true)
                .build();
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), subscriptionId, PaymentType.INITIAL,
                PaymentResult.FAILED, null, "DECLINED", Instant.now()
        );

        when(processedEventReadOutPort.existsById(event.eventId())).thenReturn(false);
        when(subscriptionPolicyService.getSubscriptionOrThrow(subscriptionId)).thenReturn(subscription);
        when(subscriptionPolicyService.getActivePlan(1)).thenReturn(plan);
        when(subscriptionWriteOutPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.handlePaymentCompleted(event);

        assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
        verify(subscriptionStatusEventService, times(1)).enqueueStatusChanged(any(), any(), any(), any());
        verify(processedEventWriteOutPort, times(1)).save(any());
    }

    @Test
    void shouldIgnoreAlreadyProcessedRenewalAttempt() {
        SubscriptionPolicyService subscriptionPolicyService = mock(SubscriptionPolicyService.class);
        SubscriptionWriteOutPort subscriptionWriteOutPort = mock(SubscriptionWriteOutPort.class);
        RenewalAttemptReadOutPort renewalAttemptReadOutPort = mock(RenewalAttemptReadOutPort.class);
        RenewalAttemptWriteOutPort renewalAttemptWriteOutPort = mock(RenewalAttemptWriteOutPort.class);
        ProcessedEventReadOutPort processedEventReadOutPort = mock(ProcessedEventReadOutPort.class);
        ProcessedEventWriteOutPort processedEventWriteOutPort = mock(ProcessedEventWriteOutPort.class);
        SubscriptionStatusEventService subscriptionStatusEventService = mock(SubscriptionStatusEventService.class);
        HandlePaymentCompletedService service = new HandlePaymentCompletedService(
                subscriptionPolicyService,
                subscriptionWriteOutPort,
                renewalAttemptReadOutPort,
                renewalAttemptWriteOutPort,
                processedEventReadOutPort,
                processedEventWriteOutPort,
                subscriptionStatusEventService
        );

        UUID subscriptionId = UUID.randomUUID();
        UUID paymentRequestId = UUID.randomUUID();
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");
        subscription.setId(subscriptionId);
        subscription.activateInitialPayment(30);
        subscription.setCurrentPeriodStart(Instant.now().minusSeconds(86400));
        subscription.setCurrentPeriodEnd(Instant.now());
        subscription.setNextRenewalDate(Instant.now());
        Plan plan = Plan.builder()
                .id(1)
                .code("basic")
                .name("Basic")
                .price(new BigDecimal("9.99"))
                .currency("USD")
                .billingPeriodDays(30)
                .active(true)
                .build();
        RenewalAttempt renewalAttempt = RenewalAttempt.completed(subscriptionId, "2026-04-11", paymentRequestId);
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), paymentRequestId, subscriptionId, PaymentType.RENEWAL,
                PaymentResult.SUCCEEDED, "provider", null, Instant.now()
        );

        when(processedEventReadOutPort.existsById(event.eventId())).thenReturn(false);
        when(subscriptionPolicyService.getSubscriptionOrThrow(subscriptionId)).thenReturn(subscription);
        when(subscriptionPolicyService.getActivePlan(1)).thenReturn(plan);
        when(renewalAttemptReadOutPort.findByPaymentRequestId(paymentRequestId)).thenReturn(Optional.of(renewalAttempt));

        service.handlePaymentCompleted(event);

        verify(subscriptionWriteOutPort, never()).save(any());
        verify(renewalAttemptWriteOutPort, never()).save(any());
        verify(subscriptionStatusEventService, never()).enqueueStatusChanged(any(), any(), any(), any());
        verify(processedEventWriteOutPort, times(1)).save(any());
    }
}
