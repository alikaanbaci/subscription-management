package com.subsys.payment.application.port.in;

import com.subsys.contracts.PaymentRequestedEvent;

public interface ProcessPaymentRequestUseCase {
    void processPaymentRequest(PaymentRequestedEvent event);
}
