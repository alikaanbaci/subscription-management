ALTER TABLE outbox_events
    ADD COLUMN claimed_at TIMESTAMPTZ;

CREATE INDEX idx_payment_outbox_status_claimed_at
    ON outbox_events (status, claimed_at);
