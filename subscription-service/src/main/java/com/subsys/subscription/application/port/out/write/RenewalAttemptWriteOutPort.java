package com.subsys.subscription.application.port.out.write;

import com.subsys.subscription.domain.RenewalAttempt;

public interface RenewalAttemptWriteOutPort {
    RenewalAttempt save(RenewalAttempt renewalAttempt);
}
