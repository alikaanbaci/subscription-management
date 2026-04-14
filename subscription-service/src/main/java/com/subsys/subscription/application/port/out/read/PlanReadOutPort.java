package com.subsys.subscription.application.port.out.read;

import com.subsys.subscription.domain.Plan;

import java.util.Optional;

public interface PlanReadOutPort {
    Optional<Plan> findById(Integer planId);
}
