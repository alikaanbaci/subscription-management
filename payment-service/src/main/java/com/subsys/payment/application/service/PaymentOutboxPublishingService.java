package com.subsys.payment.application.service;

import com.subsys.payment.application.port.in.PublishPendingPaymentOutboxUseCase;
import com.subsys.payment.application.port.out.read.OutboxReadOutPort;
import com.subsys.payment.application.port.out.write.OutboxWriteOutPort;
import com.subsys.payment.application.port.out.write.PaymentCompletedMessageOutPort;
import com.subsys.payment.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOutboxPublishingService implements PublishPendingPaymentOutboxUseCase {
    private final OutboxReadOutPort outboxReadOutPort;
    private final OutboxWriteOutPort outboxWriteOutPort;
    private final PaymentCompletedMessageOutPort paymentCompletedMessageOutPort;
    @org.springframework.beans.factory.annotation.Value("${app.outbox.claim-timeout-ms:30000}")
    private long claimTimeoutMs;

    @Override
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxReadOutPort.claimPendingEvents(20, Instant.now().minusMillis(claimTimeoutMs));
        for (OutboxEvent event : pendingEvents) {
            try {
                paymentCompletedMessageOutPort.publish(event.getId().toString(), event.getPayload());
                event.markPublished();
            } catch (Exception exception) {
                event.markRetry();
                log.warn("payment_outbox_publish_failed eventId={}", event.getId(), exception);
            }
            outboxWriteOutPort.save(event);
        }
    }
}
