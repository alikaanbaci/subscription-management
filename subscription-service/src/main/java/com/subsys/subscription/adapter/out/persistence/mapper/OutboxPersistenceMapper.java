package com.subsys.subscription.adapter.out.persistence.mapper;

import com.subsys.subscription.adapter.out.persistence.entity.OutboxEventEntity;
import com.subsys.subscription.domain.OutboxEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OutboxPersistenceMapper {
    OutboxEvent toDomain(OutboxEventEntity entity);

    OutboxEventEntity toEntity(OutboxEvent domain);
}
