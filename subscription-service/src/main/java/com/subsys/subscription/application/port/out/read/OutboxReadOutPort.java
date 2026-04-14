package com.subsys.subscription.application.port.out.read;

import com.subsys.subscription.domain.OutboxEvent;

import java.time.Instant;
import java.util.List;

public interface OutboxReadOutPort {
    List<OutboxEvent> claimPendingEvents(int limit, Instant staleBefore);
}
