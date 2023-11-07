package com.app.crashdump.datastore.impl;

import com.app.crashdump.constant.ReportConstants;
import com.app.crashdump.datastore.Storage;
import com.app.crashdump.dto.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class RedisStorage implements Storage {
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ReactiveHashOperations<String, String, Long> reactiveHashOperations;
    private final ReactiveSetOperations<String, String> reactiveSetOperations;

    @Autowired
    RedisStorage(ReactiveRedisTemplate<String, String> redisTemplate, ReactiveHashOperations<String, String, Long> reactiveHashOperations, ReactiveSetOperations<String, String> reactiveSetOperations) {
        this.redisTemplate = redisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveSetOperations = reactiveSetOperations;
    }


    /**
     * Calls redis increment operation for the count of given key.
     * Redis is responsible to keep this increment operation Atomic.
     *
     * @param hash .
     * @param key .
     * @return .
     */
    public Mono<Long> increment(String hash, String key) {
        return reactiveHashOperations.increment(hash, key, 1L);
    }

    /**
     * We append the new value in the Set for the given key.
     * Duplicate values are ignored.
     *
     * @param key .
     * @param value .
     * @return .
     */
    public Mono<Long> userErrorCount(String key, String value) {
        return reactiveSetOperations.add(key, value);
    }

    /**
     * Retrieves value against each hash key.
     *
     * @param hash .
     * @return .
     */
    public Flux<KeyValue> getTotalReport(String hash) {
        return reactiveHashOperations.entries(hash)
                .map(entry -> new KeyValue(entry.getKey(), entry.getValue()));
    }

    /**
     * Retrieves all keys from redis and returns key and total unique count.
     *
     * @return .
     */
    public Flux<KeyValue> getAffectedUsersReport() {
        return redisTemplate.keys(ReportConstants.SEARCH_ALL)
                .filter(val -> !val.equalsIgnoreCase(ReportConstants.TOTAL_REPORT))
                .flatMap(key -> reactiveSetOperations.members(key)
                        .count()
                        .map(value -> new KeyValue(key, value)));
    }
}
