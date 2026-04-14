package com.subsys.subscription.application.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class BillingPeriodService {
    private static final DateTimeFormatter BILLING_KEY_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC);

    public String toBillingPeriodKey(Instant renewalDate) {
        return BILLING_KEY_FORMATTER.format(renewalDate);
    }
}
