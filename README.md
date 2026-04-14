# Subscription Management System

The project focuses on correctness of subscription lifecycle, asynchronous payment handling, idempotent consumers, and explicit distributed consistency boundaries.

## Overview

The system is split into three Spring Boot services:

- `subscription-service`
- `payment-service`
- `notification-service`

Core business rule:

- a subscription must never become `ACTIVE` before payment succeeds

Core reliability decisions:

- local transaction + outbox instead of `2PC`
- asynchronous event propagation through Kafka
- idempotent consumers with `processed_events`
- duplicate-safe renewal scheduling
- multi-instance-safe outbox claiming with `IN_PROGRESS` + `FOR UPDATE SKIP LOCKED`
- explicit Kafka retry and DLQ handling

## Business Flows

### Subscription creation

1. Client calls `POST /subscriptions`.
2. `subscription-service` creates the subscription as `PENDING_PAYMENT`.
3. In the same local transaction, it writes a `payment.requested` outbox record.
4. The outbox publisher sends the event to Kafka.
5. `payment-service` processes the payment request asynchronously.
6. `payment-service` publishes `payment.completed`.
7. `subscription-service` activates the subscription on success, or cancels it on initial payment failure.
8. `notification-service` sends mock notifications for lifecycle outcomes.

### Cancellation

1. Client calls `POST /subscriptions/{id}/cancel`.
2. `subscription-service` disables future renewals.
3. The subscription moves to `CANCELLED`.
4. No further renewal charge is created for that subscription.

### Renewal

1. `subscription-service` scheduler scans due subscriptions.
2. It creates one logical renewal attempt per billing cycle.
3. It emits a renewal `payment.requested` event through the outbox.
4. `payment-service` processes the renewal payment.
5. Success extends the billing period.
6. Failure moves the subscription to `PAST_DUE`.

## Service Responsibilities

### `subscription-service`

- owns subscription lifecycle and state transitions
- validates plan existence and subscription invariants
- schedules renewals
- reacts to payment results
- publishes subscription domain integration events

### `payment-service`

- consumes `payment.requested`
- creates one payment attempt per logical payment request
- calls a mock payment provider
- publishes `payment.completed`

### `notification-service`

- consumes payment and subscription outcome events
- creates mock notification deliveries
- logs delivery attempts

## Architecture

Each service follows a hexagonal structure:

- `adapter.in`: HTTP, Kafka, scheduler entry points
- `application.port.in`: use-case contracts
- `application.service`: orchestration and business application flow
- `application.port.out`: persistence and messaging contracts
- `adapter.out`: JPA and Kafka adapters
- `domain`: domain objects and state transition logic

Persistence rules:

- JPA persistence classes use the `Entity` suffix
- entity classes stay inside persistence adapters
- ports and application services do not expose entities
- entity/domain conversion is handled with MapStruct

## State Model

Subscription states:

- `PENDING_PAYMENT`
- `ACTIVE`
- `CANCELLED`
- `PAST_DUE`

State rules:

- creation starts in `PENDING_PAYMENT`
- initial payment success -> `ACTIVE`
- initial payment failure -> `CANCELLED`
- user cancellation stops future renewals
- renewal success extends the current period
- renewal failure -> `PAST_DUE`

## Reliability Model

### Outbox publishing

- business state and integration event are written in the same local transaction
- publisher first claims rows as `IN_PROGRESS`
- claim uses PostgreSQL row locking with `FOR UPDATE SKIP LOCKED`
- failed publish resets the row back to `PENDING`
- stale claimed rows can be retried through `claimed_at` timeout handling

### Consumer idempotency

- `payment-service` protects processing with `payment_request_id` uniqueness and `processed_events`
- `subscription-service` protects payment result handling and renewal duplication
- `notification-service` protects duplicate deliveries with `processed_events` and notification uniqueness

### Retry and DLQ

- Kafka listeners use explicit retry/backoff configuration
- non-retryable parsing failures are routed to DLQ topics
- DLQ topics:
  - `payment.requested.dlq`
  - `payment.completed.dlq`
  - `subscription.status.changed.dlq`

## Technology Stack

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- PostgreSQL
- Kafka
- Flyway
- MapStruct
- Lombok
- Docker Compose
- JUnit 5

## Repository Layout

```text
.
├── subscription-service
├── payment-service
├── notification-service
├── shared-contracts
├── docs
├── http
└── postman
```

## Running the System

### Full stack with Docker

Start everything:

```bash
docker compose up --build -d
```

Stop and remove containers, networks, and volumes:

```bash
docker compose down -v
```

Services:

- `subscription-service`: `http://localhost:8080`
- `payment-service`: `http://localhost:8081`
- `notification-service`: `http://localhost:8082`
- `kafka-ui`: `http://localhost:8085`

### Local development

Start infrastructure only:

```bash
docker compose up -d postgres kafka kafka-ui zookeeper
```

Run a service locally:

```bash
mvn -pl subscription-service -am spring-boot:run
mvn -pl payment-service -am spring-boot:run
mvn -pl notification-service -am spring-boot:run
```

Important local note:

- when running services locally, Kafka bootstrap servers must point to `localhost:9092`
- when running inside Docker, services use `kafka:9092`

## Testing

Run the full test suite:

```bash
mvn test
```

Build all modules:

```bash
mvn clean install
```

## API Summary

Base business API is exposed by `subscription-service`.

### `POST /subscriptions`

Creates a subscription and starts async payment processing.

`paymentMethodToken` in this case implementation is a mock control input, not a real card token or raw PAN-like payment secret. It is intentionally used to drive deterministic payment-provider outcomes for local testing and scenario simulation.

Example request:

```json
{
  "userId": "d41afddf-c06c-4b6a-b8cb-5fd7c0a9e3a3",
  "planId": 2,
  "paymentMethodToken": "tok_success"
}
```

Example immediate response:

```json
{
  "subscriptionId": "08294ff4-e860-4066-9e69-e390c787f890",
  "userId": "d41afddf-c06c-4b6a-b8cb-5fd7c0a9e3a3",
  "planId": 2,
  "status": "PENDING_PAYMENT",
  "currentPeriodStart": null,
  "currentPeriodEnd": null,
  "nextRenewalDate": null,
  "autoRenew": true,
  "message": "Subscription is being created"
}
```

### `GET /subscriptions/{id}`

Returns the latest subscription state.

### `POST /subscriptions/{id}/cancel`

Cancels the subscription and prevents future renewals.

### `GET /actuator/health`

Available on all services.

## Seeded Plans

The system seeds three plans on startup:

| planId | code | name | price | currency | billingPeriodDays |
|---|---|---|---:|---|---:|
| `1` | `basic` | Basic Plan | `9.99` | `USD` | `30` |
| `2` | `pro` | Pro Plan | `19.99` | `USD` | `30` |
| `3` | `enterprise` | Enterprise Plan | `49.99` | `USD` | `30` |

## Documentation

Detailed documents remain under `docs/`:

- [Architecture](docs/architecture.md)
- [API Guide](docs/api.md)
- [Database Model](docs/db-model.md)
- [Distributed Transaction Strategy](docs/distributed-transaction.md)
- [Assumptions](docs/assumptions.md)
- [Runbook](docs/runbook.md)

Request helpers:

- [HTTP Requests](http/subscription-api.http)
- [Postman Collection](postman/subsys.postman_collection.json)

## Assumptions and Non-Goals

Key assumptions:

- initial payment failure cancels the subscription
- cancellation stops future renewals
- one user can have only one open subscription per plan
- payment provider is mocked
- `paymentMethodToken` is treated as a mock scenario selector for the simulated payment provider, not as real card data
- notification delivery is mocked

Out of scope:

- real payment gateway integration
- real email/SMS provider integration
- refunds and proration
- plan upgrades and downgrades
- Kubernetes and service mesh
- CQRS or event sourcing

## Design Tradeoffs

- `2PC` was intentionally avoided in favor of outbox-based eventual consistency.
- `Saga` orchestration and event sourcing were intentionally not selected because they add complexity without improving the core case outcome.
- PostgreSQL-specific `FOR UPDATE SKIP LOCKED` is used deliberately for safe outbox claiming in multi-instance deployments.
