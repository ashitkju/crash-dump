package com.app.crashdump.controller;

import com.app.crashdump.dto.KeyValue;
import reactor.core.publisher.Flux;

public interface IReports {
    Flux<KeyValue> getTotal();
    Flux<KeyValue> getAffectedUsers();
}
