package com.subsys.subscription.application.port.out.write;

import com.subsys.subscription.domain.Subscription;

public interface SubscriptionWriteOutPort {
    Subscription save(Subscription subscription);
}
