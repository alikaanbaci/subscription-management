package com.subsys.subscription.application.service;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.port.in.CancelSubscriptionUseCase;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelSubscriptionService implements CancelSubscriptionUseCase {
    private final SubscriptionPolicyService subscriptionPolicyService;
    private final SubscriptionWriteOutPort subscriptionWriteOutPort;
    private final SubscriptionStatusEventService subscriptionStatusEventService;

    @Override
    @Transactional
    public Subscription cancelSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionPolicyService.getSubscriptionOrThrow(subscriptionId);

        SubscriptionStatus oldStatus = subscription.cancelByUser();

        Subscription updatedSubscription = subscriptionWriteOutPort.save(subscription);

        subscriptionStatusEventService.enqueueStatusChanged(updatedSubscription, oldStatus, "USER_CANCELLED", UUID.randomUUID());

        return updatedSubscription;
    }
}
