package com.subsys.subscription.adapter.out.persistence;

import com.subsys.subscription.adapter.out.persistence.mapper.ProcessedEventPersistenceMapper;
import com.subsys.subscription.adapter.out.persistence.repository.ProcessedEventJpaRepository;
import com.subsys.subscription.application.port.out.read.ProcessedEventReadOutPort;
import com.subsys.subscription.application.port.out.write.ProcessedEventWriteOutPort;
import com.subsys.subscription.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessedEventPersistenceAdapter implements ProcessedEventReadOutPort, ProcessedEventWriteOutPort {
    private final ProcessedEventJpaRepository processedEventJpaRepository;
    private final ProcessedEventPersistenceMapper processedEventPersistenceMapper;

    @Override
    public boolean existsById(UUID eventId) {
        return processedEventJpaRepository.existsById(eventId);
    }

    @Override
    public ProcessedEvent save(ProcessedEvent processedEvent) {
        return processedEventPersistenceMapper.toDomain(
                processedEventJpaRepository.save(processedEventPersistenceMapper.toEntity(processedEvent))
        );
    }
}
