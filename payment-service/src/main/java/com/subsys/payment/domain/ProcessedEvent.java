package com.subsys.payment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProcessedEvent {
    private static final String PAYMENT_REQUEST_CONSUMER = "payment-request-consumer";

    private UUID eventId;
    private String consumerName;
    private Instant processedAt;
    private UUID correlationId;

    public static ProcessedEvent paymentRequest(UUID eventId, UUID correlationId) {
        return ProcessedEvent.builder()
                .eventId(eventId)
                .consumerName(PAYMENT_REQUEST_CONSUMER)
                .processedAt(Instant.now())
                .correlationId(correlationId)
                .build();
    }
}
