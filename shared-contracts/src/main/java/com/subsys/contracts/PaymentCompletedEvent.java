package com.subsys.contracts;


import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(UUID eventId, UUID correlationId, UUID paymentRequestId, UUID subscriptionId,
                                    PaymentType paymentType, PaymentResult result, String providerReference,
                                    String failureReason, Instant processedAt) {
        public static PaymentCompletedEvent forSucceeded(PaymentRequestedEvent paymentRequest, String providerReference) {
                return new PaymentCompletedEvent(
                        UUID.randomUUID(),
                        paymentRequest.correlationId(),
                        paymentRequest.paymentRequestId(),
                        paymentRequest.subscriptionId(),
                        paymentRequest.paymentType(),
                        PaymentResult.SUCCEEDED,
                        providerReference,
                        null,
                        Instant.now());
        }

        public static PaymentCompletedEvent forFailed(PaymentRequestedEvent paymentRequest, String failureReason) {
                return new PaymentCompletedEvent(
                        UUID.randomUUID(),
                        paymentRequest.correlationId(),
                        paymentRequest.paymentRequestId(),
                        paymentRequest.subscriptionId(),
                        paymentRequest.paymentType(),
                        PaymentResult.FAILED,
                        null,
                        failureReason,
                        Instant.now());
        }
}
