package com.subsys.subscription.application.service;

import com.subsys.subscription.application.command.CreateSubscriptionCommand;
import com.subsys.subscription.application.port.in.CreateSubscriptionUseCase;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.domain.PaymentRequestDispatch;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateSubscriptionService implements CreateSubscriptionUseCase {
    private final SubscriptionWriteOutPort subscriptionWriteOutPort;
    private final SubscriptionPolicyService subscriptionPolicyService;
    private final PaymentRequestEventService paymentRequestEventService;

    @Override
    @Transactional
    public Subscription createSubscription(CreateSubscriptionCommand command) {
        Plan plan = subscriptionPolicyService.getActivePlan(command.planId());

        subscriptionPolicyService.validateNoOpenSubscription(command.userId(), plan.getId());

        Subscription pendingSubscription = Subscription.pendingPayment(command.userId(), plan.getId(), command.paymentMethodToken());

        Subscription subscription = subscriptionWriteOutPort.save(pendingSubscription);

        PaymentRequestDispatch dispatch = paymentRequestEventService.enqueueInitialPaymentRequested(subscription, plan);

        log.info("subscription_created subscriptionId={} correlationId={} status={}", subscription.getId(), dispatch.correlationId(), subscription.getStatus());

        return subscription;
    }
}
