package com.subsys.subscription.adapter.out.messaging;

import com.subsys.contracts.TopicNames;
import com.subsys.subscription.application.port.out.read.OutboxReadOutPort;
import com.subsys.subscription.application.port.out.write.OutboxWriteOutPort;
import com.subsys.subscription.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxReadOutPort outboxReadOutPort;
    private final OutboxWriteOutPort outboxWriteOutPort;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @org.springframework.beans.factory.annotation.Value("${app.outbox.claim-timeout-ms:30000}")
    private long claimTimeoutMs;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms:2000}")
    @Transactional
    public void publishPendingEvents() {
        Instant staleBefore = Instant.now().minusMillis(claimTimeoutMs);

        for (OutboxEvent event : outboxReadOutPort.claimPendingEvents(20, staleBefore)) {
            try {
                kafkaTemplate.send(resolveTopic(event.getEventType()), event.getId().toString(), event.getPayload()).get();
                event.markPublished();
            } catch (Exception exception) {
                event.markRetry();
                log.warn("outbox_publish_failed eventId={} eventType={}", event.getId(), event.getEventType(), exception);
            }
            outboxWriteOutPort.save(event);
        }
    }

    private String resolveTopic(String eventType) {
        return switch (eventType) {
            case "PaymentRequested" -> TopicNames.PAYMENT_REQUESTED;
            case "SubscriptionStatusChanged" -> TopicNames.SUBSCRIPTION_STATUS_CHANGED;
            default -> throw new IllegalArgumentException("Unsupported event type: " + eventType);
        };
    }
}
