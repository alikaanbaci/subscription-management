package com.subsys.payment.application.port.out.write;

public interface PaymentCompletedMessageOutPort {
    void publish(String key, String payload) throws Exception;
}
