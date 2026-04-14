package com.subsys.notification.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.SubscriptionStatusChangedEvent;
import com.subsys.contracts.TopicNames;
import com.subsys.notification.application.port.in.HandlePaymentCompletedNotificationUseCase;
import com.subsys.notification.application.port.in.HandleSubscriptionStatusChangedNotificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final HandlePaymentCompletedNotificationUseCase handlePaymentCompletedNotificationUseCase;
    private final HandleSubscriptionStatusChangedNotificationUseCase handleSubscriptionStatusChangedNotificationUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TopicNames.PAYMENT_COMPLETED, groupId = "notification-service")
    public void onPaymentCompleted(String payload) throws JsonProcessingException {
        handlePaymentCompletedNotificationUseCase.handlePaymentCompleted(objectMapper.readValue(payload, PaymentCompletedEvent.class));
    }

    @KafkaListener(topics = TopicNames.SUBSCRIPTION_STATUS_CHANGED, groupId = "notification-service")
    public void onSubscriptionChanged(String payload) throws JsonProcessingException {
        handleSubscriptionStatusChangedNotificationUseCase.handleSubscriptionStatusChanged(objectMapper.readValue(payload, SubscriptionStatusChangedEvent.class));
    }
}
