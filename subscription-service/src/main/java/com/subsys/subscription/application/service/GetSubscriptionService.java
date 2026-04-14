package com.subsys.subscription.application.service;

import com.subsys.subscription.application.port.in.GetSubscriptionUseCase;
import com.subsys.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSubscriptionService implements GetSubscriptionUseCase {
    private final SubscriptionPolicyService subscriptionPolicyService;

    @Override
    @Transactional(readOnly = true)
    public Subscription getSubscription(UUID subscriptionId) {

        return subscriptionPolicyService.getSubscriptionOrThrow(subscriptionId);
    }
}
