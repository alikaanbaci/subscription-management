package com.subsys.subscription.application.port.in;

import com.subsys.subscription.domain.Subscription;

import java.util.UUID;

public interface GetSubscriptionUseCase {
    Subscription getSubscription(UUID subscriptionId);
}
