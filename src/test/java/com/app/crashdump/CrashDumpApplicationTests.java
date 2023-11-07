package com.app.crashdump;

import com.app.crashdump.dto.CrashDetails;
import com.app.crashdump.dto.KeyValue;
import com.redis.testcontainers.RedisContainer;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CrashDumpApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;
    @Autowired
    @Qualifier("redisTemplateLong")
    private ReactiveRedisTemplate<String, Long> reactiveRedisTemplate;
    @Container
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        REDIS_CONTAINER.start();
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379)
                .toString());
    }

    @Test
    void autoConfigRedisTest() {
        Assert.assertTrue(REDIS_CONTAINER.isRunning());
    }

    @Test
    public void integrationTests1() {
        // Test createTask endpoint
        webTestClient.post().uri("/collect").contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dummyData())).exchange().expectStatus().isOk().expectBody(String.class).value(created -> {
                    Assertions.assertThat(created).isNotNull();
                });

        // Test getTaskById endpoint
        webTestClient.get().uri("/report/total").exchange().expectStatus().isOk().expectBodyList(KeyValue.class).value(total -> {
            Assertions.assertThat(total).hasSize(3);
            for (var data : total) {
                if (data.key().equals("Null")) {
                    Assertions.assertThat(data.value()).isEqualTo(1);
                } else if (data.key().equals("File IO")) {
                    Assertions.assertThat(data.value()).isEqualTo(3);
                } else if (data.key().equals("Crash")) {
                    Assertions.assertThat(data.value()).isEqualTo(3);
                }
            }
        });

        // Test getAllTasks endpoint
        webTestClient.get().uri("/report/affected-users").exchange().expectStatus().isOk().expectBodyList(KeyValue.class).value(users -> {
            Assertions.assertThat(users).hasSize(3);
            for (var data : users) {
                if (data.key().equals("Null")) {
                    Assertions.assertThat(data.value()).isEqualTo(1);
                } else if (data.key().equals("File IO")) {
                    Assertions.assertThat(data.value()).isEqualTo(3);
                } else if (data.key().equals("Crash")) {
                    Assertions.assertThat(data.value()).isEqualTo(2);
                }
            }
        });
    }

    @Test
    public void integrationTests2() {
        // Test collect endpoint
        webTestClient.post().uri("/collect").contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dummyData2())).exchange().expectStatus().isOk().expectBody(String.class).value(created -> {
                    Assertions.assertThat(created).isNotNull();
                });

        // Test getTotal endpoint
        webTestClient.get().uri("/report/total").exchange().expectStatus().isOk().expectBodyList(KeyValue.class).value(total -> {
            Assertions.assertThat(total).hasSize(3);
            for (var data : total) {
                if (data.key().equals("Null")) {
                    Assertions.assertThat(data.value()).isEqualTo(3);
                } else if (data.key().equals("File IO")) {
                    Assertions.assertThat(data.value()).isEqualTo(5);
                } else if (data.key().equals("Crash")) {
                    Assertions.assertThat(data.value()).isEqualTo(6);
                }
            }
        });

        // Test getAffectedUsers endpoint
        webTestClient.get().uri("/report/affected-users").exchange().expectStatus().isOk().expectBodyList(KeyValue.class).value(users -> {
            Assertions.assertThat(users).hasSize(3);
            for (var data : users) {
                if (data.key().equals("Null")) {
                    Assertions.assertThat(data.value()).isEqualTo(3);
                } else if (data.key().equals("File IO")) {
                    Assertions.assertThat(data.value()).isEqualTo(5);
                } else if (data.key().equals("Crash")) {
                    Assertions.assertThat(data.value()).isEqualTo(3);
                }
            }
        });
    }


    private List<CrashDetails> dummyData() {
        CrashDetails task1 = new CrashDetails("Josh", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Null");
        CrashDetails task2 = new CrashDetails("Josh", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "File IO");
        CrashDetails task3 = new CrashDetails("Ram", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "File IO");
        CrashDetails task4 = new CrashDetails("Shalini", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Crash");
        CrashDetails task5 = new CrashDetails("Shalini", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Crash");
        CrashDetails task6 = new CrashDetails("Tiger", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "File IO");
        CrashDetails task7 = new CrashDetails("Ram", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Crash");
        return List.of(task1, task2, task3, task4, task5, task6, task7);
    }

    private Object dummyData2() {
        CrashDetails task1 = new CrashDetails("Jason", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Null");
        CrashDetails task2 = new CrashDetails("Justin", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "File IO");
        CrashDetails task3 = new CrashDetails("Obama", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Crash");
        CrashDetails task4 = new CrashDetails("Obama", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Crash");
        CrashDetails task5 = new CrashDetails("Obama", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Crash");
        CrashDetails task6 = new CrashDetails("Obama", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "File IO");
        CrashDetails task7 = new CrashDetails("Justin", "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)", "Null");
        return List.of(task1, task2, task3, task4, task5, task6, task7);
    }
}