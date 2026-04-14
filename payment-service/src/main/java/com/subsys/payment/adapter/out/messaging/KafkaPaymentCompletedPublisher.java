package com.subsys.payment.adapter.out.messaging;

import com.subsys.contracts.TopicNames;
import com.subsys.payment.application.port.out.write.PaymentCompletedMessageOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPaymentCompletedPublisher implements PaymentCompletedMessageOutPort {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String key, String payload) throws Exception {
        kafkaTemplate.send(TopicNames.PAYMENT_COMPLETED, key, payload).get();
    }
}
