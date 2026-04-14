package com.subsys.payment.adapter.out.persistence.repository;

import com.subsys.payment.adapter.out.persistence.entity.PaymentAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentAttemptJpaRepository extends JpaRepository<PaymentAttemptEntity, UUID> {
    Optional<PaymentAttemptEntity> findByPaymentRequestId(UUID paymentRequestId);
}
