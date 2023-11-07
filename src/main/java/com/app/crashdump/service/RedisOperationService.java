package com.app.crashdump.service;

import com.app.crashdump.constant.ReportConstants;
import com.app.crashdump.datastore.impl.RedisStorage;
import com.app.crashdump.dto.CrashDetails;
import com.app.crashdump.dto.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RedisOperationService {
    private final RedisStorage redisStorage;
    @Autowired
    RedisOperationService(RedisStorage redisStorage) {
        this.redisStorage = redisStorage;
    }

    /**
     * Handles updates to the real time report values in redis.
     *
     * @param chunk .
     * @return .
     */
    public Mono<Void> updateReports(CrashDetails chunk) {
        var totalMono = redisStorage.increment(ReportConstants.TOTAL_REPORT, chunk.errorMessage());
        var affectedUserMono = redisStorage.userErrorCount(chunk.errorMessage(), chunk.userId());
        return Mono.when(totalMono, affectedUserMono);
    }

    public Flux<KeyValue> getTotal() {
        return redisStorage.getTotalReport(ReportConstants.TOTAL_REPORT);
    }

    public Flux<KeyValue> getAffectedUsers() {
        return redisStorage.getAffectedUsersReport();
    }
}
