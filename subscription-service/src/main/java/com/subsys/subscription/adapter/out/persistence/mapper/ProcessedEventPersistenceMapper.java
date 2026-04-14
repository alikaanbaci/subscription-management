package com.subsys.subscription.adapter.out.persistence.mapper;

import com.subsys.subscription.adapter.out.persistence.entity.ProcessedEventEntity;
import com.subsys.subscription.domain.ProcessedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProcessedEventPersistenceMapper {
    ProcessedEvent toDomain(ProcessedEventEntity entity);

    ProcessedEventEntity toEntity(ProcessedEvent domain);
}
