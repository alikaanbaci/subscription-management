package com.subsys.subscription.application.service;

import com.subsys.subscription.application.port.in.ScheduleRenewalsUseCase;
import com.subsys.subscription.application.port.out.read.SubscriptionReadOutPort;
import com.subsys.subscription.application.port.out.write.RenewalAttemptWriteOutPort;
import com.subsys.subscription.domain.PaymentRequestDispatch;
import com.subsys.subscription.domain.Plan;
import com.subsys.subscription.domain.RenewalAttempt;
import com.subsys.subscription.domain.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ScheduleRenewalsService implements ScheduleRenewalsUseCase {
    private final SubscriptionReadOutPort subscriptionReadOutPort;
    private final RenewalAttemptWriteOutPort renewalAttemptWriteOutPort;
    private final SubscriptionPolicyService subscriptionPolicyService;
    private final BillingPeriodService billingPeriodService;
    private final PaymentRequestEventService paymentRequestEventService;
    private final TransactionTemplate renewalTransactionTemplate;

    public ScheduleRenewalsService(
            SubscriptionReadOutPort subscriptionReadOutPort,
            RenewalAttemptWriteOutPort renewalAttemptWriteOutPort,
            SubscriptionPolicyService subscriptionPolicyService,
            BillingPeriodService billingPeriodService,
            PaymentRequestEventService paymentRequestEventService,
            PlatformTransactionManager transactionManager
    ) {
        this.subscriptionReadOutPort = subscriptionReadOutPort;
        this.renewalAttemptWriteOutPort = renewalAttemptWriteOutPort;
        this.subscriptionPolicyService = subscriptionPolicyService;
        this.billingPeriodService = billingPeriodService;
        this.paymentRequestEventService = paymentRequestEventService;
        this.renewalTransactionTemplate = new TransactionTemplate(transactionManager);
        this.renewalTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public void scheduleRenewals() {
        Instant now = Instant.now();
        List<Subscription> dueSubscriptions = subscriptionReadOutPort.findDueRenewals(now);

        for (Subscription subscription : dueSubscriptions) {
            try {
                renewalTransactionTemplate.executeWithoutResult(status -> processRenewal(subscription, now));
            } catch (DataIntegrityViolationException ignored) {
                logDuplicateClaim(subscription);
            } catch (Exception exception) {
                log.error(
                        "renewal_processing_failed subscriptionId={} planId={} nextRenewalDate={} errorType={} errorMessage={}",
                        subscription.getId(),
                        subscription.getPlanId(),
                        subscription.getNextRenewalDate(),
                        exception.getClass().getSimpleName(),
                        exception.getMessage(),
                        exception
                );
            }
        }
    }

    private void processRenewal(Subscription subscription, Instant now) {
        if (!subscription.isRenewable(now)) {
            return;
        }

        Plan plan = subscriptionPolicyService.getActivePlan(subscription.getPlanId());
        String billingPeriodKey = billingPeriodService.toBillingPeriodKey(subscription.getNextRenewalDate());
        UUID paymentRequestId = UUID.randomUUID();
        RenewalAttempt renewalAttempt = renewalAttemptWriteOutPort.save(
                RenewalAttempt.requested(subscription.getId(), billingPeriodKey, paymentRequestId)
        );
        PaymentRequestDispatch dispatch = paymentRequestEventService.enqueueRenewalPaymentRequested(subscription, plan, renewalAttempt);

        log.info(
                "renewal_requested subscriptionId={} paymentRequestId={} billingPeriodKey={} correlationId={}",
                subscription.getId(),
                dispatch.paymentRequestId(),
                billingPeriodKey,
                dispatch.correlationId()
        );
    }

    private void logDuplicateClaim(Subscription subscription) {
        log.info(
                "renewal_duplicate_claim_ignored subscriptionId={} billingPeriodKey={}",
                subscription.getId(),
                billingPeriodService.toBillingPeriodKey(subscription.getNextRenewalDate())
        );
    }
}
