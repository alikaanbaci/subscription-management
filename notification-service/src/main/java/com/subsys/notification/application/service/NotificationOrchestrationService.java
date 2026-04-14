package com.subsys.notification.application.service;

import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.SubscriptionStatusChangedEvent;
import com.subsys.notification.application.port.in.HandlePaymentCompletedNotificationUseCase;
import com.subsys.notification.application.port.in.HandleSubscriptionStatusChangedNotificationUseCase;
import com.subsys.notification.application.port.out.read.NotificationProcessedEventReadOutPort;
import com.subsys.notification.application.port.out.write.NotificationDeliveryWriteOutPort;
import com.subsys.notification.application.port.out.write.NotificationProcessedEventWriteOutPort;
import com.subsys.notification.domain.NotificationDelivery;
import com.subsys.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationOrchestrationService implements HandlePaymentCompletedNotificationUseCase, HandleSubscriptionStatusChangedNotificationUseCase {
    private final NotificationDeliveryWriteOutPort notificationDeliveryWriteOutPort;
    private final NotificationProcessedEventReadOutPort notificationProcessedEventReadOutPort;
    private final NotificationProcessedEventWriteOutPort notificationProcessedEventWriteOutPort;

    @Override
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        if (notificationProcessedEventReadOutPort.existsById(event.eventId())) return;

        String notificationType = "PAYMENT_" + event.result().name();

        notificationDeliveryWriteOutPort.save(NotificationDelivery.paymentResult(event));

        notificationProcessedEventWriteOutPort.save(ProcessedEvent.paymentCompletedNotification(event));

        log.info("notification_sent type={} subscriptionId={} correlationId={}", notificationType, event.subscriptionId(), event.correlationId());
    }

    @Override
    @Transactional
    public void handleSubscriptionStatusChanged(SubscriptionStatusChangedEvent event) {
        if (!shouldProcessed(event)) return;

        String notificationType = "SUBSCRIPTION_" + event.getNewStatus().name();

        notificationDeliveryWriteOutPort.save(NotificationDelivery.subscriptionStatus(event));

        notificationProcessedEventWriteOutPort.save(ProcessedEvent.subscriptionStatusNotification(event));

        log.info("notification_sent type={} subscriptionId={} correlationId={}", notificationType, event.getSubscriptionId(), event.getCorrelationId());
    }

    private boolean shouldProcessed(SubscriptionStatusChangedEvent event) {
        boolean exist = notificationProcessedEventReadOutPort.existsById(event.getEventId());

        if (exist) {
            log.info("subscription_status_notification_duplicate_ignored eventId={} correlationId={}", event.getEventId(), event.getCorrelationId());
        }

        return !exist;
    }
}
