CREATE TABLE plans (
    id INTEGER PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    price NUMERIC(19,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    billing_period_days INTEGER NOT NULL,
    active BOOLEAN NOT NULL
);

INSERT INTO plans (id, code, name, price, currency, billing_period_days, active) VALUES
    (1, 'basic', 'Basic Plan', 9.99, 'USD', 30, true),
    (2, 'pro', 'Pro Plan', 19.99, 'USD', 30, true),
    (3, 'enterprise', 'Enterprise Plan', 49.99, 'USD', 30, true);

ALTER TABLE subscriptions
    ADD CONSTRAINT fk_subscriptions_plan
    FOREIGN KEY (plan_id) REFERENCES plans(id);
