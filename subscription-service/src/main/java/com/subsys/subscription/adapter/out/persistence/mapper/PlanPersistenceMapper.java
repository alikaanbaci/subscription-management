package com.subsys.subscription.adapter.out.persistence.mapper;

import com.subsys.subscription.adapter.out.persistence.entity.PlanEntity;
import com.subsys.subscription.domain.Plan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanPersistenceMapper {
    Plan toDomain(PlanEntity entity);

    PlanEntity toEntity(Plan domain);
}
