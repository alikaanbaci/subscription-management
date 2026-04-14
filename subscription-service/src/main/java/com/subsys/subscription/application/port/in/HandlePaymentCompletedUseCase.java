package com.subsys.subscription.application.port.in;

import com.subsys.contracts.PaymentCompletedEvent;

public interface HandlePaymentCompletedUseCase {
    void handlePaymentCompleted(PaymentCompletedEvent event);
}
