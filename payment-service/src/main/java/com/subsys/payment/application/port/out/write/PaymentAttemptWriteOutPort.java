package com.subsys.payment.application.port.out.write;

import com.subsys.payment.domain.PaymentAttempt;

public interface PaymentAttemptWriteOutPort {
    PaymentAttempt save(PaymentAttempt paymentAttempt);
}
