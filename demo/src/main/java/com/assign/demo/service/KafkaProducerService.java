package com.assign.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.assign.demo.model.UserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "user-events";

    @Retryable(value = KafkaException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendUserEvent(UserDetails user) {
    try {
        kafkaTemplate.send("user-events", String.valueOf(user.getId()), user);
        log.info("📤 Sending user data to Kafka: {}", user);
    } catch (KafkaException e) {
        log.error("Failed to send user data to Kafka: {}", e.getMessage());
        throw e; // Allow retry mechanism to work
    }
}
}
