package com.subsys.subscription.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {
    private Integer id;
    private String code;
    private String name;
    private BigDecimal price;
    private String currency;
    private int billingPeriodDays;
    private boolean active;
}
