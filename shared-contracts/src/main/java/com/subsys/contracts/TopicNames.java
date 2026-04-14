package com.subsys.contracts;

public final class TopicNames {
    public static final String PAYMENT_REQUESTED = "payment.requested";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String SUBSCRIPTION_STATUS_CHANGED = "subscription.status.changed";
    public static final String PAYMENT_REQUESTED_DLQ = "payment.requested.dlq";
    public static final String PAYMENT_COMPLETED_DLQ = "payment.completed.dlq";
    public static final String SUBSCRIPTION_STATUS_CHANGED_DLQ = "subscription.status.changed.dlq";

    private TopicNames() {
    }
}
