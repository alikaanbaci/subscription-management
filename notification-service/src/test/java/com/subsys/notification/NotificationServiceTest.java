package com.subsys.notification;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.contracts.SubscriptionStatusChangedEvent;
import com.subsys.notification.application.port.out.read.NotificationProcessedEventReadOutPort;
import com.subsys.notification.application.port.out.write.NotificationDeliveryWriteOutPort;
import com.subsys.notification.application.port.out.write.NotificationProcessedEventWriteOutPort;
import com.subsys.notification.application.service.NotificationOrchestrationService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Test
    void shouldIgnoreDuplicateSubscriptionStatusEvent() {
        NotificationDeliveryWriteOutPort notificationDeliveryWriteOutPort = mock(
                NotificationDeliveryWriteOutPort.class);
        NotificationProcessedEventReadOutPort notificationProcessedEventReadOutPort = mock(
                NotificationProcessedEventReadOutPort.class);
        NotificationProcessedEventWriteOutPort notificationProcessedEventWriteOutPort = mock(
                NotificationProcessedEventWriteOutPort.class);
        NotificationOrchestrationService service = new NotificationOrchestrationService(
                notificationDeliveryWriteOutPort,
                notificationProcessedEventReadOutPort,
                notificationProcessedEventWriteOutPort);
        SubscriptionStatusChangedEvent event = SubscriptionStatusChangedEvent.statusChanged(
                UUID.randomUUID(),
                UUID.randomUUID(),
                SubscriptionStatus.PENDING_PAYMENT,
                SubscriptionStatus.ACTIVE,
                "INITIAL_PAYMENT_SUCCEEDED");

        when(notificationProcessedEventReadOutPort.existsById(event.getEventId())).thenReturn(false, true);

        service.handleSubscriptionStatusChanged(event);
        service.handleSubscriptionStatusChanged(event);

        verify(notificationDeliveryWriteOutPort, times(1)).save(any());
        verify(notificationProcessedEventWriteOutPort, times(1)).save(any());
        verify(notificationProcessedEventReadOutPort, times(2)).existsById(event.getEventId());
    }
}
