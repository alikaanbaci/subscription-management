package com.subsys.notification.application.port.in;

import com.subsys.contracts.SubscriptionStatusChangedEvent;

public interface HandleSubscriptionStatusChangedNotificationUseCase {
    void handleSubscriptionStatusChanged(SubscriptionStatusChangedEvent event);
}
