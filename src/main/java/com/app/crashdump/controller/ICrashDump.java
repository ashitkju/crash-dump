package com.app.crashdump.controller;

import com.app.crashdump.dto.CrashDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICrashDump {
    Mono<String> collect(Flux<CrashDetails> payload);
}
