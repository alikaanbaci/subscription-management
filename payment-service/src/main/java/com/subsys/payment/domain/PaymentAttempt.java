package com.subsys.payment.domain;

import com.subsys.contracts.PaymentRequestedEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PaymentAttempt {
    public enum DecisionResult {
        SUCCEEDED,
        FAILED
    }

    private UUID id;
    private UUID paymentRequestId;
    private UUID subscriptionId;
    private UUID userId;
    private Integer planId;
    private String paymentType;
    private String billingPeriodKey;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String providerReference;
    private String failureReason;
    private UUID correlationId;
    private Instant createdAt;
    private Instant updatedAt;

    public void markProcessing() {
        status = "PROCESSING";
    }

    public void markSucceeded(String providerReference) {
        status = "SUCCEEDED";
        this.providerReference = providerReference;
        failureReason = null;
    }

    public void markFailed(String failureReason) {
        status = "FAILED";
        this.failureReason = failureReason;
    }

    public DecisionResult applyDecision(MockPaymentDecision decision) {
        if (decision.succeeded()) {
            markSucceeded(decision.providerReference());
            return DecisionResult.SUCCEEDED;
        }

        markFailed(decision.failureReason());
        return DecisionResult.FAILED;
    }

    public static PaymentAttempt fromPaymentRequest(PaymentRequestedEvent event) {
        return PaymentAttempt.builder()
                .id(UUID.randomUUID())
                .paymentRequestId(event.paymentRequestId())
                .subscriptionId(event.subscriptionId())
                .userId(event.userId())
                .planId(event.planId())
                .paymentType(event.paymentType().name())
                .billingPeriodKey(event.billingPeriodKey())
                .amount(event.amount())
                .currency(event.currency())
                .status("PENDING")
                .correlationId(event.correlationId())
                .build();
    }
}
