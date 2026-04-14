package com.subsys.subscription.application.service;

import com.subsys.contracts.PaymentRequestedEvent;
import com.subsys.subscription.application.port.out.write.OutboxWriteOutPort;
import com.subsys.subscription.domain.PaymentRequestDispatch;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.RenewalAttempt;
import com.subsys.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentRequestEventService {
        private final OutboxWriteOutPort outboxWriteOutPort;

        public PaymentRequestDispatch enqueueInitialPaymentRequested(Subscription subscription, Plan plan) {
                UUID correlationId = UUID.randomUUID();

                PaymentRequestedEvent event = createInitialPaymentEvent(correlationId, subscription, plan);

                outboxWriteOutPort.paymentRequested(subscription.getId(), UUID.randomUUID().toString(), event);

                return new PaymentRequestDispatch(correlationId, subscription.getInitialPaymentRequestId());
        }

        public PaymentRequestDispatch enqueueRenewalPaymentRequested(Subscription subscription, Plan plan, RenewalAttempt renewalAttempt) {
                UUID correlationId = UUID.randomUUID();

                PaymentRequestedEvent event = createRenewalPaymentEvent(correlationId, subscription, plan, renewalAttempt);

                outboxWriteOutPort.paymentRequested(subscription.getId(), UUID.randomUUID().toString(), event);

                return new PaymentRequestDispatch(correlationId, renewalAttempt.getPaymentRequestId());
        }

        private PaymentRequestedEvent createInitialPaymentEvent(UUID correlationId, Subscription subscription, Plan plan) {

                return PaymentRequestedEvent.initialPayment(
                                correlationId,
                                subscription.getInitialPaymentRequestId(),
                                subscription.getId(),
                                subscription.getUserId(),
                                subscription.getPlanId(),
                                plan.getPrice(),
                                plan.getCurrency(),
                                subscription.getPaymentMethodToken());
        }

        private PaymentRequestedEvent createRenewalPaymentEvent(UUID correlationId, Subscription subscription, Plan plan, RenewalAttempt renewalAttempt) {

                return PaymentRequestedEvent.renewalPayment(
                                correlationId,
                                renewalAttempt.getPaymentRequestId(),
                                subscription.getId(),
                                subscription.getUserId(),
                                subscription.getPlanId(),
                                renewalAttempt.getBillingPeriodKey(),
                                plan.getPrice(),
                                plan.getCurrency(),
                                subscription.getPaymentMethodToken());
        }
}
