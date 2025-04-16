package com.assign.demo.service;

import com.assign.demo.model.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDetails(1L, "Test User", "test@example.com");
    }

    @Test
    void sendUserEvent_Success() {
        // Create a mock SendResult
        SendResult<String, Object> mockSendResult = mock(SendResult.class);

        // Create a completed CompletableFuture
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mockSendResult);

        // Mock KafkaTemplate to return the future
        when(kafkaTemplate.send(eq("user-events"), anyString(), eq(testUser)))
            .thenReturn(future);

        kafkaProducerService.sendUserEvent(testUser);

        verify(kafkaTemplate).send("user-events", String.valueOf(testUser.getId()), testUser);
    }

    @Test
    void sendUserEvent_KafkaException() {
        when(kafkaTemplate.send(eq("user-events"), anyString(), eq(testUser)))
            .thenThrow(new KafkaException("Test Kafka exception"));

        assertThrows(KafkaException.class, () -> {
            kafkaProducerService.sendUserEvent(testUser);
        });

        verify(kafkaTemplate).send("user-events", String.valueOf(testUser.getId()), testUser);
    }
}
