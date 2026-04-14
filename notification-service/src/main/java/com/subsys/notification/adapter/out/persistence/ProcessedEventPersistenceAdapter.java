package com.subsys.notification.adapter.out.persistence;

import com.subsys.notification.adapter.out.persistence.mapper.ProcessedEventPersistenceMapper;
import com.subsys.notification.adapter.out.persistence.repository.ProcessedEventJpaRepository;
import com.subsys.notification.application.port.out.read.NotificationProcessedEventReadOutPort;
import com.subsys.notification.application.port.out.write.NotificationProcessedEventWriteOutPort;
import com.subsys.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessedEventPersistenceAdapter implements NotificationProcessedEventReadOutPort, NotificationProcessedEventWriteOutPort {
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
