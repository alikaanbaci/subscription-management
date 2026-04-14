package com.subsys.subscription.adapter.out.persistence.repository;

import com.subsys.subscription.adapter.out.persistence.entity.RenewalAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RenewalAttemptJpaRepository extends JpaRepository<RenewalAttemptEntity, UUID> {
    Optional<RenewalAttemptEntity> findByPaymentRequestId(UUID paymentRequestId);
}
