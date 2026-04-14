package com.subsys.payment.adapter.in.scheduling;

import com.subsys.payment.application.port.in.PublishPendingPaymentOutboxUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentOutboxPublisher {
    private final PublishPendingPaymentOutboxUseCase publishPendingPaymentOutboxUseCase;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms:2000}")
    public void publishPendingEvents() {
        publishPendingPaymentOutboxUseCase.publishPendingEvents();
    }
}
