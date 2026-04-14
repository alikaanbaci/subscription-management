package com.subsys.payment.domain;

public record MockPaymentDecision(boolean succeeded, String providerReference, String failureReason) {
}
