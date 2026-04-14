package com.subsys.subscription.adapter.out.persistence;

import com.subsys.subscription.adapter.out.persistence.mapper.PlanPersistenceMapper;
import com.subsys.subscription.adapter.out.persistence.repository.PlanJpaRepository;
import com.subsys.subscription.application.port.out.read.PlanReadOutPort;
import com.subsys.subscription.domain.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlanPersistenceAdapter implements PlanReadOutPort {
    private final PlanJpaRepository planJpaRepository;
    private final PlanPersistenceMapper planPersistenceMapper;

    @Override
    public Optional<Plan> findById(Integer planId) {
        return planJpaRepository.findById(planId).map(planPersistenceMapper::toDomain);
    }
}
