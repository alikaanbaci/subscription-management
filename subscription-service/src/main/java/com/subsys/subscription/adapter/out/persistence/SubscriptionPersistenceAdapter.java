package com.subsys.subscription.adapter.out.persistence;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.adapter.out.persistence.entity.SubscriptionEntity;
import com.subsys.subscription.adapter.out.persistence.mapper.SubscriptionPersistenceMapper;
import com.subsys.subscription.adapter.out.persistence.repository.SubscriptionJpaRepository;
import com.subsys.subscription.application.port.out.read.SubscriptionReadOutPort;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionPersistenceAdapter implements SubscriptionReadOutPort, SubscriptionWriteOutPort {
    private final SubscriptionJpaRepository subscriptionJpaRepository;
    private final SubscriptionPersistenceMapper subscriptionPersistenceMapper;

    @Override
    public boolean existsByUserIdAndPlanIdAndStatuses(UUID userId, Integer planId, Set<SubscriptionStatus> statuses) {
        return subscriptionJpaRepository.existsByUserIdAndPlanIdAndStatusIn(userId, planId, statuses);
    }

    @Override
    public Optional<Subscription> findById(UUID subscriptionId) {
        return subscriptionJpaRepository.findById(subscriptionId).map(subscriptionPersistenceMapper::toDomain);
    }

    @Override
    public List<Subscription> findDueRenewals(Instant dueDate) {
        return subscriptionJpaRepository.findTop50ByStatusAndAutoRenewTrueAndNextRenewalDateLessThanEqual(SubscriptionStatus.ACTIVE, dueDate)
                .stream()
                .map(subscriptionPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entityToSave = subscriptionPersistenceMapper.toEntity(subscription);

        SubscriptionEntity savedEntity = subscriptionJpaRepository.save(entityToSave);

        return subscriptionPersistenceMapper.toDomain(savedEntity);
    }
}
