package com.subsys.subscription.adapter.out.persistence.mapper;

import com.subsys.subscription.adapter.out.persistence.entity.SubscriptionEntity;
import com.subsys.subscription.domain.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionPersistenceMapper {
    Subscription toDomain(SubscriptionEntity entity);

    SubscriptionEntity toEntity(Subscription domain);
}
