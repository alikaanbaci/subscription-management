package com.subsys.subscription;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.application.service.CancelSubscriptionService;
import com.subsys.subscription.application.service.SubscriptionPolicyService;
import com.subsys.subscription.application.service.SubscriptionStatusEventService;
import com.subsys.subscription.domain.Subscription;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelSubscriptionServiceTest {

    @Test
    void shouldCancelSubscriptionAndPublishStatusChange() {
        SubscriptionPolicyService subscriptionPolicyService = mock(SubscriptionPolicyService.class);
        SubscriptionWriteOutPort subscriptionWriteOutPort = mock(SubscriptionWriteOutPort.class);
        SubscriptionStatusEventService subscriptionStatusEventService = mock(SubscriptionStatusEventService.class);
        CancelSubscriptionService service = new CancelSubscriptionService(
                subscriptionPolicyService,
                subscriptionWriteOutPort,
                subscriptionStatusEventService
        );

        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = Subscription.pendingPayment(UUID.randomUUID(), 1, "tok_success");
        subscription.setId(subscriptionId);
        subscription.activateInitialPayment(30);

        when(subscriptionPolicyService.getSubscriptionOrThrow(subscriptionId)).thenReturn(subscription);
        when(subscriptionWriteOutPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Subscription result = service.cancelSubscription(subscriptionId);

        assertEquals(SubscriptionStatus.CANCELLED, result.getStatus());
        verify(subscriptionWriteOutPort, times(1)).save(subscription);
        verify(subscriptionStatusEventService, times(1)).enqueueStatusChanged(any(Subscription.class), any(SubscriptionStatus.class), any(String.class), any(UUID.class));
    }
}
