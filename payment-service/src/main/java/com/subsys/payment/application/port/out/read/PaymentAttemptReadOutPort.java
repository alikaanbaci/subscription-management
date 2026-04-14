package com.subsys.payment.application.port.out.read;

import com.subsys.payment.domain.PaymentAttempt;

import java.util.Optional;
import java.util.UUID;

public interface PaymentAttemptReadOutPort {
    Optional<PaymentAttempt> findByPaymentRequestId(UUID paymentRequestId);
}
