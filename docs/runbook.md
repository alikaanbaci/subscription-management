# Runbook

## Prerequisites

- Docker
- Java 17+
- Maven 3.9+

## Infrastructure

Start the full stack:

```bash
docker compose up --build -d
```

## Ports

- `subscription-service`: `8080`
- `payment-service`: `8081`
- `notification-service`: `8082`
- `kafka-ui`: `8085`

## Tests

```bash
mvn test
```

## Troubleshooting

- If Flyway fails on startup, ensure the target database exists.
- If events are not flowing, check Kafka UI and verify that services inside Docker point to `kafka:9092`.
- If renewal does not trigger quickly enough, lower the scheduler delay in the local configuration.
- If a consumer cannot process a record after retry, inspect the related DLQ topic in Kafka UI:
  - `payment.requested.dlq`
  - `payment.completed.dlq`
  - `subscription.status.changed.dlq`
