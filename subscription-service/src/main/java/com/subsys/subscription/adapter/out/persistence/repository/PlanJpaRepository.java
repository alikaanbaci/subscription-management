package com.subsys.subscription.adapter.out.persistence.repository;

import com.subsys.subscription.adapter.out.persistence.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanJpaRepository extends JpaRepository<PlanEntity, Integer> {
}
