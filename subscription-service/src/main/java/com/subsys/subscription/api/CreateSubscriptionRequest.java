package com.subsys.subscription.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull UUID userId,
        @NotNull Integer planId,
        @NotBlank String paymentMethodToken
) {
}
