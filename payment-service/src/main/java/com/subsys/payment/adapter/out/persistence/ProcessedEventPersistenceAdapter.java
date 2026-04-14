package com.subsys.payment.adapter.out.persistence;

import com.subsys.payment.adapter.out.persistence.mapper.ProcessedEventPersistenceMapper;
import com.subsys.payment.adapter.out.persistence.repository.ProcessedEventJpaRepository;
import com.subsys.payment.application.port.out.read.ProcessedEventReadOutPort;
import com.subsys.payment.application.port.out.write.ProcessedEventWriteOutPort;
import com.subsys.payment.domain.ProcessedEvent;
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
