package com.subsys.subscription.application.service;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.port.out.read.PlanReadOutPort;
import com.subsys.subscription.application.port.out.read.SubscriptionReadOutPort;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.Subscription;
import com.subsys.subscription.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionPolicyService {
    private static final Set<SubscriptionStatus> OPEN_STATUSES = Set.of(
            SubscriptionStatus.PENDING_PAYMENT,
            SubscriptionStatus.ACTIVE,
            SubscriptionStatus.PAST_DUE
    );

    private final SubscriptionReadOutPort subscriptionReadOutPort;
    private final PlanReadOutPort planReadOutPort;

    public void validateNoOpenSubscription(UUID userId, Integer planId) {
        if (subscriptionReadOutPort.existsByUserIdAndPlanIdAndStatuses(userId, planId, OPEN_STATUSES)) {
            throw new IllegalStateException("An open subscription already exists for this user and plan");
        }
    }

    public Subscription getSubscriptionOrThrow(UUID subscriptionId) {
        return subscriptionReadOutPort.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }

    public Plan getActivePlan(Integer planId) {
        Plan plan = planReadOutPort.findById(planId).orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));

        if (!plan.isActive()) {
            throw new IllegalArgumentException("Plan is not active: " + planId);
        }

        return plan;
    }
}
