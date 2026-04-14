package com.subsys.payment.adapter.out.persistence;

import com.subsys.payment.adapter.out.persistence.entity.PaymentAttemptEntity;
import com.subsys.payment.adapter.out.persistence.mapper.PaymentAttemptPersistenceMapper;
import com.subsys.payment.adapter.out.persistence.repository.PaymentAttemptJpaRepository;
import com.subsys.payment.application.port.out.read.PaymentAttemptReadOutPort;
import com.subsys.payment.application.port.out.write.PaymentAttemptWriteOutPort;
import com.subsys.payment.domain.PaymentAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentAttemptPersistenceAdapter implements PaymentAttemptReadOutPort, PaymentAttemptWriteOutPort {
    private final PaymentAttemptJpaRepository paymentAttemptJpaRepository;
    private final PaymentAttemptPersistenceMapper paymentAttemptPersistenceMapper;

    @Override
    public Optional<PaymentAttempt> findByPaymentRequestId(UUID paymentRequestId) {
        Optional<PaymentAttemptEntity> entityOptional = paymentAttemptJpaRepository.findByPaymentRequestId(paymentRequestId);

        return  entityOptional.map(paymentAttemptPersistenceMapper::toDomain);
    }

    @Override
    public PaymentAttempt save(PaymentAttempt paymentAttempt) {
        PaymentAttemptEntity entityToSave = paymentAttemptPersistenceMapper.toEntity(paymentAttempt);

        PaymentAttemptEntity savedEntity = paymentAttemptJpaRepository.save(entityToSave);

        return paymentAttemptPersistenceMapper.toDomain(savedEntity);
    }
}
