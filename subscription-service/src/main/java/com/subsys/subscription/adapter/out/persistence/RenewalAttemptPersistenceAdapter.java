package com.subsys.subscription.adapter.out.persistence;

import com.subsys.subscription.adapter.out.persistence.mapper.RenewalAttemptPersistenceMapper;
import com.subsys.subscription.adapter.out.persistence.repository.RenewalAttemptJpaRepository;
import com.subsys.subscription.application.port.out.read.RenewalAttemptReadOutPort;
import com.subsys.subscription.application.port.out.write.RenewalAttemptWriteOutPort;
import com.subsys.subscription.domain.RenewalAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RenewalAttemptPersistenceAdapter implements RenewalAttemptReadOutPort, RenewalAttemptWriteOutPort {
    private final RenewalAttemptJpaRepository renewalAttemptJpaRepository;
    private final RenewalAttemptPersistenceMapper renewalAttemptPersistenceMapper;

    @Override
    public Optional<RenewalAttempt> findByPaymentRequestId(UUID paymentRequestId) {
        return renewalAttemptJpaRepository.findByPaymentRequestId(paymentRequestId).map(renewalAttemptPersistenceMapper::toDomain);
    }

    @Override
    public RenewalAttempt save(RenewalAttempt renewalAttempt) {
        return renewalAttemptPersistenceMapper.toDomain(
                renewalAttemptJpaRepository.save(renewalAttemptPersistenceMapper.toEntity(renewalAttempt))
        );
    }
}
