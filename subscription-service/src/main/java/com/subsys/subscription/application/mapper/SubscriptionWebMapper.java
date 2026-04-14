package com.subsys.subscription.application.mapper;

import com.subsys.subscription.api.CreateSubscriptionRequest;
import com.subsys.subscription.api.SubscriptionResponse;
import com.subsys.subscription.application.command.CreateSubscriptionCommand;
import com.subsys.subscription.domain.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionWebMapper {
    public CreateSubscriptionCommand toCommand(CreateSubscriptionRequest request) {
        return new CreateSubscriptionCommand(request.userId(), request.planId(), request.paymentMethodToken());
    }

    public SubscriptionResponse toResponse(Subscription subscription, String message) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getUserId(),
                subscription.getPlanId(),
                subscription.getStatus(),
                subscription.getCurrentPeriodStart(),
                subscription.getCurrentPeriodEnd(),
                subscription.getNextRenewalDate(),
                subscription.isAutoRenew(),
                message
        );
    }
}
