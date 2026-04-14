package com.subsys.payment.service;

import com.subsys.payment.domain.MockPaymentDecision;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentProvider {
    public MockPaymentDecision process(String paymentMethodToken) {
        if (paymentMethodToken != null && paymentMethodToken.toLowerCase().contains("fail")) {
            return new MockPaymentDecision(false, null, "PAYMENT_PROVIDER_DECLINED");
        }
        return new MockPaymentDecision(true, "mock-" + UUID.randomUUID(), null);
    }
}
