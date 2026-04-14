# Assumptions

- Initial payment failure cancels the subscription.
- Cancellation prevents future renewals but does not revoke access before the current period ends.
- A user may have only one open subscription per plan at a time.
- Payment provider is mocked and deterministic.
- `paymentMethodToken` is used only as a mock scenario selector for deterministic payment simulation. In this case implementation it does not represent raw card data or a real external gateway credential.
- Notification delivery is mocked and does not affect business correctness.
- Refunds, proration, plan upgrades, and manual retries are out of scope.
- `PAST_DUE` is used as the internal representation of the case's "passive" renewal failure state.
