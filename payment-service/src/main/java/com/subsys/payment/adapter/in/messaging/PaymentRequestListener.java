package com.subsys.payment.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subsys.contracts.PaymentRequestedEvent;
import com.subsys.contracts.TopicNames;
import com.subsys.payment.application.port.in.ProcessPaymentRequestUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestListener {
    private final ProcessPaymentRequestUseCase processPaymentRequestUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TopicNames.PAYMENT_REQUESTED, groupId = "payment-service")
    public void onMessage(String payload) throws JsonProcessingException {
        PaymentRequestedEvent event = objectMapper.readValue(payload, PaymentRequestedEvent.class);

        log.info("payment_requested_received eventId={} correlationId={}", event.eventId(), event.correlationId());

        processPaymentRequestUseCase.processPaymentRequest(event);
    }
}
