package com.assignment.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {
    @Autowired
    private AuditService auditService;

    @KafkaHandler
    private void handle(String action) {
        auditService.createAudit(action);
    }

    @KafkaListener(topics="${spring.kafka.topic}", containerFactory = "eventListenerFactory")
    public void receive(@Payload String action) {
        log.info("Received {}", action);
        handle(action);
    }
}
