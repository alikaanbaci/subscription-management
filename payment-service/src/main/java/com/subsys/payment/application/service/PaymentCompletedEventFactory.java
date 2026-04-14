package com.subsys.payment.application.service;

import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.PaymentRequestedEvent;
import com.subsys.payment.domain.MockPaymentDecision;
import com.subsys.payment.domain.PaymentAttempt;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedEventFactory {
    public PaymentCompletedEvent from(PaymentRequestedEvent requestEvent,
                                      PaymentAttempt.DecisionResult result,
                                      MockPaymentDecision decision) {
        return switch (result) {
            case SUCCEEDED -> PaymentCompletedEvent.forSucceeded(requestEvent, decision.providerReference());
            case FAILED -> PaymentCompletedEvent.forFailed(requestEvent, decision.failureReason());
        };
    }
}
