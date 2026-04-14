package com.subsys.contracts;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(UUID eventId, UUID correlationId, UUID paymentRequestId, UUID subscriptionId,
                                    UUID userId, Integer planId, PaymentType paymentType, String billingPeriodKey,
                                    BigDecimal amount, String currency, String paymentMethodToken,
                                    Instant requestedAt) {
        public static PaymentRequestedEvent initialPayment(UUID correlationId, UUID paymentRequestId, UUID subscriptionId,
                UUID userId, Integer planId, BigDecimal amount, String currency, String paymentMethodToken) {
                return new PaymentRequestedEvent(
                        UUID.randomUUID(),
                        correlationId,
                        paymentRequestId,
                        subscriptionId,
                        userId,
                        planId,
                        PaymentType.INITIAL,
                        null,
                        amount,
                        currency,
                        paymentMethodToken,
                        Instant.now());
        }

        public static PaymentRequestedEvent renewalPayment(UUID correlationId, UUID paymentRequestId, UUID subscriptionId,
                UUID userId, Integer planId, String billingPeriodKey, BigDecimal amount, String currency, String paymentMethodToken) {
                return new PaymentRequestedEvent(
                        UUID.randomUUID(),
                        correlationId,
                        paymentRequestId,
                        subscriptionId,
                        userId,
                        planId,
                        PaymentType.RENEWAL,
                        billingPeriodKey,
                        amount,
                        currency,
                        paymentMethodToken,
                        Instant.now());
        }
}
