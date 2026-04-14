package com.subsys.payment;

import com.subsys.payment.domain.MockPaymentDecision;
import com.subsys.payment.service.MockPaymentProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockPaymentProviderTest {

    private final MockPaymentProvider mockPaymentProvider = new MockPaymentProvider();

    @Test
    void shouldSucceedForNonFailToken() {
        MockPaymentDecision decision = mockPaymentProvider.process("tok_success");

        assertTrue(decision.succeeded());
        assertNotNull(decision.providerReference());
    }

    @Test
    void shouldFailForFailToken() {
        MockPaymentDecision decision = mockPaymentProvider.process("tok_fail");

        assertFalse(decision.succeeded());
        assertNotNull(decision.failureReason());
    }
}
