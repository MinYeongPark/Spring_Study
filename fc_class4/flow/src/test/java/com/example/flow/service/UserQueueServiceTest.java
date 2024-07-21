package com.example.flow.service;

import com.example.flow.EmbeddedRedis;
import com.example.flow.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test") // 이 테스트를 실행할 때 test profile로 실행되도록 지정
class UserQueueServiceTest {
    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 각 테스트 진행 전에 수행해야 하는 메서드
    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe(); // 테스트 시작하기 전에 정리
    }

    @Test
    void registerWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)) // 100번 사용자를 default wait 큐에 추가
                .expectNext(1L) // 1번째로 등록이라서 1을 기대함
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue("default", 101L))
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue("default", 102L))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void alreadyRegisterWaitQueue() {
        // 100번 유저 등록 후
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();

        // 또 등록하려고 할 때 에러 반환
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L))
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    void isNotAllowed() {
        StepVerifier.create(userQueueService.isAllowed("default", 100L))
                .expectNext(false) // 아무것도 없으므로 isAllowed가 false임!
                .verifyComplete();
    }

    @Test
    void isNotAllowed2() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.allowUser("default", 3L)) // 100번 1명 통과 시키고
                        .then(userQueueService.isAllowed("default", 101L))) // 101번 유저가 허용되었는지 확인
                .expectNext(false) // 101 유저는 없으므로 isAllowed가 false임!
                .verifyComplete();
    }

    @Test
    void isAllowed() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.allowUser("default", 3L)) // 100번 1명 통과 시키고
                        .then(userQueueService.isAllowed("default", 100L))) // 100번 유저가 허용되었는지 확인
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void allowUser() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.registerWaitQueue("default", 101L))
                        .then(userQueueService.registerWaitQueue("default", 102L)) // 3명 등록 후
                        .then(userQueueService.allowUser("default", 2L))) // 2명 허용
                .expectNext(2L) // 2명이 허용되었다고 expect
                .verifyComplete();
    }

    @Test
    void allowUser2() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.registerWaitQueue("default", 101L))
                        .then(userQueueService.registerWaitQueue("default", 102L)) // 3명 등록 후
                        .then(userQueueService.allowUser("default", 5L))) // 더 큰 개수를 요청하더라도 3개만 잘 들어감
                .expectNext(3L)
                .verifyComplete();
    }

    // 전부 다 허용된 후에, 새로운 유저가 다시 대기할 때에 대한 테스트
    @Test
    void allowUserAfterRegisterWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.registerWaitQueue("default", 101L))
                        .then(userQueueService.registerWaitQueue("default", 102L)) // 3명 등록 후
                        .then(userQueueService.allowUser("default", 3L)) // 3명 통과 시키고
                        .then(userQueueService.registerWaitQueue("default", 100L))) // 200번 유저를 추가로 대기시킨다면 몇번을 받아야 할까?
                .expectNext(1L) // 다시 처음부터 시작이라서 대기 인원이 1명이니까 1을 받아야 함
                .verifyComplete();
    }

    @Test
    void getRank() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.getRank("default", 100L)))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue("default", 101L)
                        .then(userQueueService.getRank("default", 101L)))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void emptyRank() {
        StepVerifier.create(userQueueService.getRank("default", 100L))
                .expectNext(-1L) // 아무것도 없으니까 -1 리턴됨
                .verifyComplete();
    }

    @Test
    void generateToken() {
        StepVerifier.create(userQueueService.generateToken("default", 100L))
                .expectNext("d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8")
                .verifyComplete();
    }

    @Test
    void isAllowedByToken() {
        StepVerifier.create(userQueueService.isAllowedByToken("default", 100L, "d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isNotAllowedByToken() {
        StepVerifier.create(userQueueService.isAllowedByToken("default", 100L, "")) // 토큰으로 빈 값을 넘겨주면
                .expectNext(false) // 해당 토큰으로 허용 여부가 false이기 때문에 false를 기대하게 됨
                .verifyComplete();
    }
}