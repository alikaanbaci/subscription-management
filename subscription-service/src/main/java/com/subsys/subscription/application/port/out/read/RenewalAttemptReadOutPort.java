package com.subsys.subscription.application.port.out.read;

import com.subsys.subscription.domain.RenewalAttempt;

import java.util.Optional;
import java.util.UUID;

public interface RenewalAttemptReadOutPort {
    Optional<RenewalAttempt> findByPaymentRequestId(UUID paymentRequestId);
}
