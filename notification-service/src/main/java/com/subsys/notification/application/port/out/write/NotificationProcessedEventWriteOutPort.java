package com.subsys.notification.application.port.out.write;

import com.subsys.notification.domain.ProcessedEvent;

public interface NotificationProcessedEventWriteOutPort {
    ProcessedEvent save(ProcessedEvent processedEvent);
}
