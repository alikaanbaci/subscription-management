package com.subsys.payment.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.subsys.contracts.TopicNames;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConsumerConfig {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final long retryBackoffMs;
    private final long retryAttempts;

    public KafkaConsumerConfig(KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${app.kafka.consumer.retry-backoff-ms:1000}") long retryBackoffMs,
                               @Value("${app.kafka.consumer.retry-attempts:3}") long retryAttempts) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryBackoffMs = retryBackoffMs;
        this.retryAttempts = retryAttempts;
    }

    @Bean
    public DefaultErrorHandler commonErrorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(resolveDlqTopic(record), record.partition()));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(retryBackoffMs, retryAttempts));
        errorHandler.addNotRetryableExceptions(JsonProcessingException.class);
        errorHandler.setRetryListeners((record, exception, deliveryAttempt) ->
                log.warn("payment_consumer_retry topic={} partition={} offset={} attempt={}",
                        record.topic(), record.partition(), record.offset(), deliveryAttempt, exception));
        return errorHandler;
    }

    private String resolveDlqTopic(ConsumerRecord<?, ?> record) {
        return switch (record.topic()) {
            case TopicNames.PAYMENT_REQUESTED -> TopicNames.PAYMENT_REQUESTED_DLQ;
            default -> record.topic() + ".dlq";
        };
    }
}
