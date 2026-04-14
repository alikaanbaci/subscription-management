package com.subsys.notification.application.port.in;

import com.subsys.contracts.PaymentCompletedEvent;

public interface HandlePaymentCompletedNotificationUseCase {
    void handlePaymentCompleted(PaymentCompletedEvent event);
}
