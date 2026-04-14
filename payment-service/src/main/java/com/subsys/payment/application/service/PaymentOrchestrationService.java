package com.subsys.payment.application.service;

import com.subsys.contracts.PaymentCompletedEvent;
import com.subsys.contracts.PaymentRequestedEvent;
import com.subsys.payment.application.port.in.ProcessPaymentRequestUseCase;
import com.subsys.payment.application.port.out.read.PaymentAttemptReadOutPort;
import com.subsys.payment.application.port.out.read.ProcessedEventReadOutPort;
import com.subsys.payment.application.port.out.write.OutboxWriteOutPort;
import com.subsys.payment.application.port.out.write.PaymentAttemptWriteOutPort;
import com.subsys.payment.application.port.out.write.ProcessedEventWriteOutPort;
import com.subsys.payment.domain.MockPaymentDecision;
import com.subsys.payment.domain.PaymentAttempt;
import com.subsys.payment.domain.ProcessedEvent;
import com.subsys.payment.service.MockPaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOrchestrationService implements ProcessPaymentRequestUseCase {
    private final PaymentAttemptReadOutPort paymentAttemptReadOutPort;
    private final PaymentAttemptWriteOutPort paymentAttemptWriteOutPort;
    private final ProcessedEventReadOutPort processedEventReadOutPort;
    private final ProcessedEventWriteOutPort processedEventWriteOutPort;
    private final OutboxWriteOutPort outboxWriteOutPort;
    private final MockPaymentProvider mockPaymentProvider;
    private final PaymentCompletedEventFactory paymentCompletedEventFactory;

    @Override
    @Transactional
    public void processPaymentRequest(PaymentRequestedEvent event) {
        if (isDuplicateEvent(event)) return;

        if (isLogicalDuplicate(event)) return;

        processPayment(event);

        markEventAsProcessed(event);
    }

    private boolean isDuplicateEvent(PaymentRequestedEvent event) {
        if (!processedEventReadOutPort.existsById(event.eventId())) {
            return false;
        }
        log.info("payment_requested_duplicate_ignored eventId={} correlationId={}", event.eventId(), event.correlationId());
        return true;
    }

    private boolean isLogicalDuplicate(PaymentRequestedEvent event) {
        Optional<PaymentAttempt> paymentAttempt = paymentAttemptReadOutPort.findByPaymentRequestId(event.paymentRequestId());

        if (paymentAttempt.isPresent()) {
            log.info("payment_request_logical_duplicate_ignored paymentRequestId={} correlationId={}", event.paymentRequestId(), event.correlationId());
            markEventAsProcessed(event);
            return true;
        }

        return false;
    }

    private void processPayment(PaymentRequestedEvent event) {
        PaymentAttempt paymentAttempt = paymentAttemptWriteOutPort.save(PaymentAttempt.fromPaymentRequest(event));
        paymentAttempt.markProcessing();

        MockPaymentDecision decision = mockPaymentProvider.process(event.paymentMethodToken());
        PaymentAttempt.DecisionResult result = paymentAttempt.applyDecision(decision);
        PaymentCompletedEvent completedEvent = paymentCompletedEventFactory.from(event, result, decision);

        paymentAttemptWriteOutPort.save(paymentAttempt);
        outboxWriteOutPort.paymentCompleted(event.paymentRequestId(), completedEvent.eventId().toString(), completedEvent);

        log.info("payment_processed paymentRequestId={} result={} correlationId={}", event.paymentRequestId(), completedEvent.result(), event.correlationId());
    }

    private void markEventAsProcessed(PaymentRequestedEvent event) {
        processedEventWriteOutPort.save(ProcessedEvent.paymentRequest(event.eventId(), event.correlationId()));
    }
}
