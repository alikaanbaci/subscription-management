package com.subsys.subscription.adapter.out.persistence.repository;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.adapter.out.persistence.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {
    boolean existsByUserIdAndPlanIdAndStatusIn(UUID userId, Integer planId, Set<SubscriptionStatus> statuses);

    Optional<SubscriptionEntity> findByInitialPaymentRequestId(UUID paymentRequestId);

    List<SubscriptionEntity> findTop50ByStatusAndAutoRenewTrueAndNextRenewalDateLessThanEqual(SubscriptionStatus status, Instant dueDate);
}
