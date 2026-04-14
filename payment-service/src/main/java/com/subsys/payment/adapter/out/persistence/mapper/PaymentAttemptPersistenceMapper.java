package com.subsys.payment.adapter.out.persistence.mapper;

import com.subsys.payment.adapter.out.persistence.entity.PaymentAttemptEntity;
import com.subsys.payment.domain.PaymentAttempt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentAttemptPersistenceMapper {
    PaymentAttempt toDomain(PaymentAttemptEntity entity);

    PaymentAttemptEntity toEntity(PaymentAttempt domain);
}
