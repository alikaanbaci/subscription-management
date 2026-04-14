package com.subsys.subscription.adapter.in.scheduling;

import com.subsys.subscription.application.port.in.ScheduleRenewalsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RenewalScheduler {
    private final ScheduleRenewalsUseCase scheduleRenewalsUseCase;

    @Scheduled(fixedDelayString = "${app.renewal.fixed-delay-ms:15000}")
    public void scheduleRenewals() {
        scheduleRenewalsUseCase.scheduleRenewals();
    }
}
