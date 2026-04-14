package com.subsys.subscription.application.command;

import java.util.UUID;

public record CreateSubscriptionCommand(
        UUID userId,
        Integer planId,
        String paymentMethodToken
) {
}
