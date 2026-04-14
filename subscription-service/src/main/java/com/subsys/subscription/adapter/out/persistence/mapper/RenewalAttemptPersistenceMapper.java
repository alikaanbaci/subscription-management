package com.subsys.subscription.adapter.out.persistence.mapper;

import com.subsys.subscription.adapter.out.persistence.entity.RenewalAttemptEntity;
import com.subsys.subscription.domain.RenewalAttempt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RenewalAttemptPersistenceMapper {
    RenewalAttempt toDomain(RenewalAttemptEntity entity);

    RenewalAttemptEntity toEntity(RenewalAttempt domain);
}
