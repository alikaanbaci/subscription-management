package com.subsys.subscription.application.port.out.read;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.domain.Subscription;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SubscriptionReadOutPort {
    boolean existsByUserIdAndPlanIdAndStatuses(UUID userId, Integer planId, Set<SubscriptionStatus> statuses);

    Optional<Subscription> findById(UUID subscriptionId);

    List<Subscription> findDueRenewals(Instant dueDate);
}
