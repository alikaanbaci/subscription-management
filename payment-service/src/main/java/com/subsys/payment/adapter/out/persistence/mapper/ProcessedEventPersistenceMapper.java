package com.subsys.payment.adapter.out.persistence.mapper;

import com.subsys.payment.adapter.out.persistence.entity.ProcessedEventEntity;
import com.subsys.payment.domain.ProcessedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProcessedEventPersistenceMapper {
    ProcessedEvent toDomain(ProcessedEventEntity entity);

    ProcessedEventEntity toEntity(ProcessedEvent domain);
}
