CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    plan_id INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    auto_renew BOOLEAN NOT NULL,
    payment_method_token VARCHAR(255) NOT NULL,
    current_period_start TIMESTAMPTZ,
    current_period_end TIMESTAMPTZ,
    next_renewal_date TIMESTAMPTZ,
    initial_payment_request_id UUID NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    cancelled_at TIMESTAMPTZ,
    status_reason VARCHAR(255)
);

CREATE UNIQUE INDEX uk_subscriptions_open_user_plan
    ON subscriptions (user_id, plan_id)
    WHERE status IN ('PENDING_PAYMENT', 'ACTIVE', 'PAST_DUE');

CREATE INDEX idx_subscriptions_next_renewal_date ON subscriptions (next_renewal_date);
CREATE INDEX idx_subscriptions_user_plan ON subscriptions (user_id, plan_id);

CREATE TABLE renewal_attempts (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL,
    billing_period_key VARCHAR(100) NOT NULL,
    payment_request_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_renewal_attempt_subscription_period UNIQUE (subscription_id, billing_period_key)
);

CREATE INDEX idx_renewal_attempt_status ON renewal_attempts (status);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_key VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    published_at TIMESTAMPTZ,
    retry_count INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_subscription_outbox_event_key UNIQUE (event_key)
);

CREATE INDEX idx_subscription_outbox_status_created_at ON outbox_events (status, created_at);

CREATE TABLE processed_events (
    event_id UUID PRIMARY KEY,
    consumer_name VARCHAR(100) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL,
    correlation_id UUID
);
