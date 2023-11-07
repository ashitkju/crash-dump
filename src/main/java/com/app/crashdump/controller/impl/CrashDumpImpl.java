package com.app.crashdump.controller.impl;

import com.app.crashdump.controller.ICrashDump;
import com.app.crashdump.dto.CrashDetails;
import com.app.crashdump.service.KafkaProducerService;
import com.app.crashdump.service.RedisOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CrashDumpImpl implements ICrashDump {
    private final KafkaProducerService kafkaProducerService;
    private final RedisOperationService redisOperationService;

    @Autowired
    public CrashDumpImpl(KafkaProducerService kafkaProducerService, RedisOperationService redisOperationService) {
        this.kafkaProducerService = kafkaProducerService;
        this.redisOperationService = redisOperationService;
    }

    /**
     * Api responsible to take crash dump logs from the request body.
     * Process the logs as it comes reactively and uploads the required data to redis for reporting purpose.
     * Also, send the message to downstream system through a kafka topic.
     *
     * @param payload .
     * @return .
     */
    @PostMapping("/collect")
    public Mono<String> collect(@RequestBody Flux<CrashDetails> payload) {
        return payload.doOnNext(chunk -> redisOperationService.updateReports(chunk)
                        .then(kafkaProducerService.sendMessageToKafka(chunk)).subscribe())
                .then(Mono.just("Payload received."));
    }
}