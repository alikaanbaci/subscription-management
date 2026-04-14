package com.subsys.subscription.application.port.out.write;

import com.subsys.subscription.domain.ProcessedEvent;

public interface ProcessedEventWriteOutPort {
    ProcessedEvent save(ProcessedEvent processedEvent);
}
