package com.subsys.subscription;

import com.subsys.contracts.SubscriptionStatus;
import com.subsys.subscription.application.command.CreateSubscriptionCommand;
import com.subsys.subscription.application.port.out.write.SubscriptionWriteOutPort;
import com.subsys.subscription.application.service.CreateSubscriptionService;
import com.subsys.subscription.domain.PaymentRequestDispatch;
import com.subsys.subscription.application.service.PaymentRequestEventService;
import com.subsys.subscription.application.service.SubscriptionPolicyService;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.Subscription;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateSubscriptionServiceTest {

    @Test
    void shouldCreatePendingSubscriptionAndEnqueueInitialPayment() {
        SubscriptionWriteOutPort subscriptionWriteOutPort = mock(SubscriptionWriteOutPort.class);
        SubscriptionPolicyService subscriptionPolicyService = mock(SubscriptionPolicyService.class);
        PaymentRequestEventService paymentRequestEventService = mock(PaymentRequestEventService.class);
        CreateSubscriptionService service = new CreateSubscriptionService(
                subscriptionWriteOutPort,
                subscriptionPolicyService,
                paymentRequestEventService
        );

        CreateSubscriptionCommand command = new CreateSubscriptionCommand(UUID.randomUUID(), 1, "tok_success");
        Plan plan = Plan.builder()
                .id(1)
                .code("basic")
                .name("Basic")
                .price(new BigDecimal("9.99"))
                .currency("USD")
                .billingPeriodDays(30)
                .active(true)
                .build();

        when(subscriptionPolicyService.getActivePlan(1)).thenReturn(plan);
        when(subscriptionWriteOutPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRequestEventService.enqueueInitialPaymentRequested(any(Subscription.class), any(Plan.class)))
                .thenReturn(new PaymentRequestDispatch(UUID.randomUUID(), UUID.randomUUID()));

        Subscription subscription = service.createSubscription(command);

        assertNotNull(subscription.getId());
        assertEquals(SubscriptionStatus.PENDING_PAYMENT, subscription.getStatus());
        verify(subscriptionPolicyService, times(1)).validateNoOpenSubscription(command.userId(), 1);
        verify(subscriptionWriteOutPort, times(1)).save(any(Subscription.class));
        verify(paymentRequestEventService, times(1)).enqueueInitialPaymentRequested(any(Subscription.class), any(Plan.class));
    }
}
