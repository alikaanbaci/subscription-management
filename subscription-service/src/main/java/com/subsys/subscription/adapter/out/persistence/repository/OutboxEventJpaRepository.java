package com.subsys.subscription.adapter.out.persistence.repository;

import com.subsys.subscription.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop20ByStatusOrderByCreatedAtAsc(String status);
}
