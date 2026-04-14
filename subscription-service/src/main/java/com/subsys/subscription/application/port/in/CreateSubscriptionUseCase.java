package com.subsys.subscription.application.port.in;

import com.subsys.subscription.application.command.CreateSubscriptionCommand;
import com.subsys.subscription.domain.Subscription;

public interface CreateSubscriptionUseCase {
    Subscription createSubscription(CreateSubscriptionCommand command);
}
