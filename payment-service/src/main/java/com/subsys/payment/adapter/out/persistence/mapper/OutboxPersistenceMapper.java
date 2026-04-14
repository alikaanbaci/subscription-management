package com.subsys.payment.adapter.out.persistence.mapper;

import com.subsys.payment.adapter.out.persistence.entity.OutboxEventEntity;
import com.subsys.payment.domain.OutboxEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OutboxPersistenceMapper {
    OutboxEvent toDomain(OutboxEventEntity entity);

    OutboxEventEntity toEntity(OutboxEvent domain);
}
