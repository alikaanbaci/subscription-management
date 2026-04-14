package com.subsys.payment.application.port.out.write;

import com.subsys.payment.domain.ProcessedEvent;

public interface ProcessedEventWriteOutPort {
    ProcessedEvent save(ProcessedEvent processedEvent);
}
