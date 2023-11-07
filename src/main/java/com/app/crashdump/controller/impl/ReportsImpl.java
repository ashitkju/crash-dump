package com.app.crashdump.controller.impl;

import com.app.crashdump.controller.IReports;
import com.app.crashdump.dto.KeyValue;
import com.app.crashdump.service.RedisOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "report")
public class ReportsImpl implements IReports {

    private final RedisOperationService redisOperationService;

    @Autowired
    ReportsImpl(RedisOperationService redisOperationService) {
        this.redisOperationService = redisOperationService;
    }

    /**
     * This API will fetch total number of times each error has appeared till now.
     *
     * @return .
     */
    @Override
    @GetMapping(path = "total", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<KeyValue> getTotal() {
        return redisOperationService.getTotal();
    }

    /**
     * This API will fetch total unique user count against each error message from redis.
     *
     * @return .
     */
    @Override
    @GetMapping(path = "affected-users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<KeyValue> getAffectedUsers() {
        return redisOperationService.getAffectedUsers();
    }
}
