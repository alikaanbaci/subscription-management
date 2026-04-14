package com.subsys.subscription.application.service;

import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.PaymentResult;
import com.subsys.contracts.PaymentType;
import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.port.in.HandlePaymentCompletedUseCase;
import com.subsys.subscription.application.port.out.read.ProcessedEventReadOutPort;
import com.subsys.subscription.application.port.out.read.RenewalAttemptReadOutPort;
import com.subsys.subscription.application.port.out.write.ProcessedEventWriteOutPort;
import com.subsys.subscription.application.port.out.write.RenewalAttemptWriteOutPort;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.ProcessedEvent;
import com.subsys.subscription.domain.RenewalAttempt;
import com.subsys.subscription.domain.Subscription;
import com.subsys.subscription.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlePaymentCompletedService implements HandlePaymentCompletedUseCase {
    private final SubscriptionPolicyService subscriptionPolicyService;
    private final SubscriptionWriteOutPort subscriptionWriteOutPort;
    private final RenewalAttemptReadOutPort renewalAttemptReadOutPort;
    private final RenewalAttemptWriteOutPort renewalAttemptWriteOutPort;
    private final ProcessedEventReadOutPort processedEventReadOutPort;
    private final ProcessedEventWriteOutPort processedEventWriteOutPort;
    private final SubscriptionStatusEventService subscriptionStatusEventService;

    @Override
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        if (processedEventReadOutPort.existsById(event.eventId())) {
            log.info("payment_completed_duplicate_ignored eventId={} correlationId={}", event.eventId(),
                    event.correlationId());
            return;
        }

        Subscription subscription = subscriptionPolicyService.getSubscriptionOrThrow(event.subscriptionId());
        if (event.paymentType() == PaymentType.INITIAL) {
            handleInitialPayment(event, subscription);
        } else {
            handleRenewalPayment(event, subscription);
        }

        ProcessedEvent paymentCompletedEvent = ProcessedEvent.paymentCompleted(event.eventId(), event.correlationId());

        processedEventWriteOutPort.save(paymentCompletedEvent);
    }

    private void handleInitialPayment(PaymentCompletedEvent event, Subscription subscription) {
        Plan plan = subscriptionPolicyService.getActivePlan(subscription.getPlanId());
        SubscriptionStatus oldStatus;

        if (event.result() == PaymentResult.SUCCEEDED) {
            oldStatus = subscription.activateInitialPayment(plan.getBillingPeriodDays());
            Subscription updatedSubscription = subscriptionWriteOutPort.save(subscription);
            subscriptionStatusEventService.enqueueStatusChanged(updatedSubscription, oldStatus,
                    "INITIAL_PAYMENT_SUCCEEDED", event.correlationId());
        } else {
            oldStatus = subscription.cancelInitialPaymentFailure();
            Subscription updatedSubscription = subscriptionWriteOutPort.save(subscription);
            subscriptionStatusEventService.enqueueStatusChanged(updatedSubscription, oldStatus,
                    "INITIAL_PAYMENT_FAILED", event.correlationId());
        }

        log.info("initial_payment_result subscriptionId={} result={} correlationId={}", subscription.getId(),
                event.result(), event.correlationId());
    }

    private void handleRenewalPayment(PaymentCompletedEvent event, Subscription subscription) {
        Plan plan = subscriptionPolicyService.getActivePlan(subscription.getPlanId());
        RenewalAttempt renewalAttempt = renewalAttemptReadOutPort.findByPaymentRequestId(event.paymentRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Renewal attempt not found"));

        if (!"REQUESTED".equals(renewalAttempt.getStatus())) {
            log.info("renewal_payment_duplicate_ignored paymentRequestId={} status={}", event.paymentRequestId(),
                    renewalAttempt.getStatus());
            return;
        }

        SubscriptionStatus oldStatus;
        if (event.result() == PaymentResult.SUCCEEDED) {
            oldStatus = subscription.extendAfterRenewalSuccess(plan.getBillingPeriodDays());
            renewalAttempt.markCompleted();
            Subscription updatedSubscription = subscriptionWriteOutPort.save(subscription);
            renewalAttemptWriteOutPort.save(renewalAttempt);
            subscriptionStatusEventService.enqueueStatusChanged(updatedSubscription, oldStatus, "RENEWAL_SUCCEEDED",
                    event.correlationId());
        } else {
            oldStatus = subscription.markRenewalFailed();
            renewalAttempt.markFailed();
            Subscription updatedSubscription = subscriptionWriteOutPort.save(subscription);
            renewalAttemptWriteOutPort.save(renewalAttempt);
            subscriptionStatusEventService.enqueueStatusChanged(updatedSubscription, oldStatus, "RENEWAL_FAILED",
                    event.correlationId());
        }

        log.info("renewal_payment_result subscriptionId={} result={} correlationId={}", subscription.getId(), event.result(), event.correlationId());
    }
}
