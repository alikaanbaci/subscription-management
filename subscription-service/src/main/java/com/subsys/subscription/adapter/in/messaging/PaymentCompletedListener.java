package com.subsys.subscription.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.TopicNames;
import com.subsys.subscription.application.port.in.HandlePaymentCompletedUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentCompletedListener {
    private final HandlePaymentCompletedUseCase handlePaymentCompletedUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TopicNames.PAYMENT_COMPLETED, groupId = "subscription-service")
    public void onMessage(String payload) throws JsonProcessingException {
        PaymentCompletedEvent event = objectMapper.readValue(payload, PaymentCompletedEvent.class);

        log.info("payment_completed_received eventId={} correlationId={}", event.eventId(), event.correlationId());

        handlePaymentCompletedUseCase.handlePaymentCompleted(event);
    }
}
