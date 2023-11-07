package com.app.crashdump.service;

import com.app.crashdump.dto.CrashDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KafkaProducerService {

    private KafkaTemplate<String, CrashDetails> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topicName;
    @Autowired
    public KafkaProducerService(KafkaTemplate<String, CrashDetails> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public Mono<Void> sendMessageToKafka(CrashDetails message) {
        return Mono.just(kafkaTemplate.send(topicName, message)).then();
    }
}

