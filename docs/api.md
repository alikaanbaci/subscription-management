# API Guide

## Base URLs

- `subscription-service`: `http://localhost:8080`
- `payment-service`: `http://localhost:8081`
- `notification-service`: `http://localhost:8082`

In this iteration, the only externally exposed business API is provided by `subscription-service`.

## Available Plans

The system seeds the following plans at startup:

| planId | code | name | price | currency | billingPeriodDays |
|---|---|---|---:|---|---:|
| `1` | `basic` | Basic Plan | `9.99` | `USD` | `30` |
| `2` | `pro` | Pro Plan | `19.99` | `USD` | `30` |
| `3` | `enterprise` | Enterprise Plan | `49.99` | `USD` | `30` |

Rules:

- `planId` is required
- unknown plans return `400 Bad Request`
- inactive plans return `400 Bad Request`

## Endpoints

### `POST /subscriptions`

Purpose:

- starts a new subscription
- creates the subscription synchronously as `PENDING_PAYMENT`
- starts the payment flow asynchronously

Request body:

Note:

- `paymentMethodToken` is not treated as real card data in this case implementation
- it is a deterministic test input used to control the mock payment provider behavior

```json
{
  "userId": "d41afddf-c06c-4b6a-b8cb-5fd7c0a9e3a3",
  "planId": 2,
  "paymentMethodToken": "tok_success"
}
```

Response:

- `202 Accepted`

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

Curl:

```bash
curl -X POST http://localhost:8080/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": "d41afddf-c06c-4b6a-b8cb-5fd7c0a9e3a3",
    "planId": 2,
    "paymentMethodToken": "tok_success"
  }'
```

Invalid plan example:

```bash
curl -X POST http://localhost:8080/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": "33333333-3333-3333-3333-333333333333",
    "planId": 999,
    "paymentMethodToken": "tok_success"
  }'
```

Example error response:

```json
{
  "type": "about:blank",
  "title": "Invalid request",
  "status": 400,
  "detail": "Plan not found: 999",
  "instance": "/subscriptions"
}
```

### `GET /subscriptions/{id}`

Purpose:

- returns the latest subscription state

Example response after successful payment:

```json
{
  "subscriptionId": "08294ff4-e860-4066-9e69-e390c787f890",
  "userId": "d41afddf-c06c-4b6a-b8cb-5fd7c0a9e3a3",
  "planId": 2,
  "status": "ACTIVE",
  "currentPeriodStart": "2026-04-10T17:40:32.929287Z",
  "currentPeriodEnd": "2026-05-10T17:40:32.929287Z",
  "nextRenewalDate": "2026-05-10T17:40:32.929287Z",
  "autoRenew": true,
  "message": "Subscription fetched"
}
```

Example response after initial payment failure:

```json
{
  "subscriptionId": "3f0d9af3-3ba5-4dd4-9749-915f00d4a1f6",
  "userId": "e2c409df-fb8d-4f52-80a7-18c79de842f1",
  "planId": 1,
  "status": "CANCELLED",
  "currentPeriodStart": null,
  "currentPeriodEnd": null,
  "nextRenewalDate": null,
  "autoRenew": false,
  "message": "Subscription fetched"
}
```

Curl:

```bash
curl http://localhost:8080/subscriptions/08294ff4-e860-4066-9e69-e390c787f890
```

### `POST /subscriptions/{id}/cancel`

Purpose:

- cancels an `ACTIVE` or `PAST_DUE` subscription
- sets `autoRenew=false`
- prevents new renewal charges from being created

Request body:

- none

Response:

```json
{
  "subscriptionId": "08294ff4-e860-4066-9e69-e390c787f890",
  "userId": "d41afddf-c06c-4b6a-b8cb-5fd7c0a9e3a3",
  "planId": 2,
  "status": "CANCELLED",
  "currentPeriodStart": "2026-04-10T17:40:32.929287Z",
  "currentPeriodEnd": "2026-05-10T17:40:32.929287Z",
  "nextRenewalDate": "2026-05-10T17:40:32.929287Z",
  "autoRenew": false,
  "message": "Subscription cancelled"
}
```

Curl:

```bash
curl -X POST http://localhost:8080/subscriptions/08294ff4-e860-4066-9e69-e390c787f890/cancel
```

## Health Endpoints

### `GET /actuator/health`

Example response:

```json
{
  "status": "UP"
}
```

Curl:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

## Example End-to-End Scenarios

### Scenario 1: Initial payment success

In this scenario, `paymentMethodToken=tok_success` causes the mock provider to return a successful result.

Request:

```bash
curl -X POST http://localhost:8080/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": "027416b4-98d3-47c7-a4b1-b9929b5018b3",
    "planId": 1,
    "paymentMethodToken": "tok_success"
  }'
```

Immediate response:

```json
{
  "subscriptionId": "53512069-4a97-4f30-9f73-f969990253ab",
  "userId": "027416b4-98d3-47c7-a4b1-b9929b5018b3",
  "planId": 1,
  "status": "PENDING_PAYMENT",
  "currentPeriodStart": null,
  "currentPeriodEnd": null,
  "nextRenewalDate": null,
  "autoRenew": true,
  "message": "Subscription is being created"
}
```

Follow-up query after asynchronous processing:

```bash
curl http://localhost:8080/subscriptions/53512069-4a97-4f30-9f73-f969990253ab
```

Final response:

```json
{
  "subscriptionId": "53512069-4a97-4f30-9f73-f969990253ab",
  "userId": "027416b4-98d3-47c7-a4b1-b9929b5018b3",
  "planId": 1,
  "status": "ACTIVE",
  "currentPeriodStart": "2026-04-10T17:39:34.772964Z",
  "currentPeriodEnd": "2026-05-10T17:39:34.772964Z",
  "nextRenewalDate": "2026-05-10T17:39:34.772964Z",
  "autoRenew": true,
  "message": "Subscription fetched"
}
```

### Scenario 2: Initial payment failure

In this scenario, `paymentMethodToken=tok_fail` causes the mock provider to return a failed result.

Request:

```bash
curl -X POST http://localhost:8080/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": "e2c409df-fb8d-4f52-80a7-18c79de842f1",
    "planId": 1,
    "paymentMethodToken": "tok_fail"
  }'
```

Final response after asynchronous processing:

```json
{
  "subscriptionId": "3f0d9af3-3ba5-4dd4-9749-915f00d4a1f6",
  "userId": "e2c409df-fb8d-4f52-80a7-18c79de842f1",
  "planId": 1,
  "status": "CANCELLED",
  "currentPeriodStart": null,
  "currentPeriodEnd": null,
  "nextRenewalDate": null,
  "autoRenew": false,
  "message": "Subscription fetched"
}
```

## Mock Payment Behavior

- `paymentMethodToken` is a test control input used by the mock payment provider
- if it contains `fail`, the payment fails
- otherwise the payment succeeds

This behavior exists to make local testing and scenario demonstration deterministic.
