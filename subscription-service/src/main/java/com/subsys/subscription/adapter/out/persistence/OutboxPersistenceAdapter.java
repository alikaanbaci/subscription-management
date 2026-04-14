package com.subsys.subscription.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subsys.subscription.adapter.out.persistence.entity.OutboxEventEntity;
import com.subsys.subscription.adapter.out.persistence.mapper.OutboxPersistenceMapper;
import com.subsys.subscription.adapter.out.persistence.repository.OutboxEventJpaRepository;
import com.subsys.subscription.application.port.out.read.OutboxReadOutPort;
import com.subsys.subscription.application.port.out.write.OutboxWriteOutPort;
import com.subsys.subscription.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxPersistenceAdapter implements OutboxReadOutPort, OutboxWriteOutPort {
    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final OutboxPersistenceMapper outboxPersistenceMapper;
    private final ObjectMapper objectMapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OutboxEvent> claimPendingEvents(int limit, Instant staleBefore) {
        Instant claimedAt = Instant.now();

        List<OutboxEventEntity> claimedEntities = entityManager.createNativeQuery("""
                SELECT *
                FROM outbox_events
                WHERE status = 'PENDING'
                   OR (status = 'IN_PROGRESS' AND claimed_at < :staleBefore)
                ORDER BY created_at
                FOR UPDATE SKIP LOCKED
                """, OutboxEventEntity.class)
                .setParameter("staleBefore", staleBefore)
                .setMaxResults(limit)
                .getResultList();

        claimedEntities.forEach(entity -> {
            entity.setStatus("IN_PROGRESS");
            entity.setClaimedAt(claimedAt);
        });

        return claimedEntities
                .stream()
                .map(outboxPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        OutboxEventEntity entityToSave = outboxPersistenceMapper.toEntity(outboxEvent);

        OutboxEventEntity savedEntity = outboxEventJpaRepository.save(entityToSave);

        return outboxPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public OutboxEvent paymentRequested(UUID aggregateId, String eventKey, Object payload) {
        try {
            return save(OutboxEvent.paymentRequested(aggregateId, eventKey, objectMapper.writeValueAsString(payload)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize outbox event", exception);
        }
    }

    @Override
    public OutboxEvent subscriptionStatusChanged(UUID aggregateId, String eventKey, Object payload) {
        try {
            return save(OutboxEvent.subscriptionStatusChanged(aggregateId, eventKey, objectMapper.writeValueAsString(payload)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize outbox event", exception);
        }
    }
}
