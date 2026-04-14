package com.subsys.subscription.domain;

import java.util.UUID;

public record PaymentRequestDispatch(
        UUID correlationId,
        UUID paymentRequestId
) {
}
