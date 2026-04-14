package com.subsys.notification.adapter.out.persistence.mapper;

import com.subsys.notification.adapter.out.persistence.entity.ProcessedEventEntity;
import com.subsys.notification.domain.ProcessedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProcessedEventPersistenceMapper {
    ProcessedEvent toDomain(ProcessedEventEntity entity);

    ProcessedEventEntity toEntity(ProcessedEvent domain);
}
