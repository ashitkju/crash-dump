package com.app.crashdump.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration.
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * Redis Template for storing Long values against they keys.
     *
     * @param factory .
     * @return .
     */
    @Bean(name = "redisTemplateLong")
    public ReactiveRedisTemplate<String, Long> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, Long> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Long> context =
                builder.key(new StringRedisSerializer())
                        .value(new GenericToStringSerializer<>(Long.class))
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new GenericToStringSerializer<>(Long.class)).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    /**
     * Set data structure to store unique values for a given key.
     *
     * @param redisTemplate .
     * @return .
     */
    @Bean
    public ReactiveSetOperations<String, String> reactiveSetOps(ReactiveRedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * Redis Hash based Key value pair storage.
     * Value is of Long type so Atomic increment operations are supported.
     *
     * @param redisTemplate .
     * @return .
     */
    @Bean
    public ReactiveHashOperations<String, String, Long> reactiveHashOps(@Qualifier("redisTemplateLong") ReactiveRedisTemplate<String, Long> redisTemplate) {
        return redisTemplate.opsForHash();
    }
}
