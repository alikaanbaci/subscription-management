CREATE TABLE payment_attempts (
    id UUID PRIMARY KEY,
    payment_request_id UUID NOT NULL,
    subscription_id UUID NOT NULL,
    user_id UUID NOT NULL,
    plan_id INTEGER NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    billing_period_key VARCHAR(100),
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    provider_reference VARCHAR(255),
    failure_reason VARCHAR(255),
    correlation_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_payment_attempt_request_id UNIQUE (payment_request_id)
);

CREATE INDEX idx_payment_attempt_subscription_id ON payment_attempts (subscription_id);
CREATE INDEX idx_payment_attempt_status ON payment_attempts (status);

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
    CONSTRAINT uk_payment_outbox_event_key UNIQUE (event_key)
);

CREATE INDEX idx_payment_outbox_status_created_at ON outbox_events (status, created_at);

CREATE TABLE processed_events (
    event_id UUID PRIMARY KEY,
    consumer_name VARCHAR(100) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL,
    correlation_id UUID
);
