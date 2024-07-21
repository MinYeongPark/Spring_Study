package com.example.flow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;

import static com.example.flow.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueueService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private final String USER_QUEUE_WAIT_KEY = "user:queue:%s:wait"; // redis는 관용적으로 key 이름에 : 을 많이 씀
                                                                     // %s : 필요 시 큐를 여러개 쓸 수 있도록 가변적으로 만듦

    private final String USER_QUEUE_WAIT_KEY_FOR_SCAN = "user:queue:*:wait"; // 운영중인 큐들을 찾아서 각각에 대해 모두 허용

    private final String USER_QUEUE_PROCEED_KEY = "user:queue:%s:proceed"; // 허용

    @Value("${scheduler.enabled}")
    private Boolean scheduling = false;

    // 대기열 등록 API
    public Mono<Long> registerWaitQueue(final String queue, final Long userId) {
        // redis sortedSet에 저장할 예정
        // - key : userId
        // - value : unix timestamp (먼저 등록한 사람이 높은 순위를 갖도록)
        // return rank

        var unixTimestamp = Instant.now().getEpochSecond(); // 등록 시점에 타임스탬프 알아냄
        return reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString(), unixTimestamp)
                .filter(i -> i) // i에 true/false 결과가 반환되게 되는데, true인 경우에 대해서만 아래로 진행 (겹치는 사용자가 없는 경우 true 반환됨)
                .switchIfEmpty(Mono.error(QUEUE_ALREADY_REGISTERED_USER.build())) // 빈 값이 내려오는 경우 = false가 리턴된 경우 = 이미 사용자가 존재하는 경우 -> 에러 반환
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString()))
                .map(i -> i >= 0 ? i+1 : i); // redis zset이 0순위부터 값이 시작하기 때문에, +1을 해주어서 대기 순번을 정하도록 설정
    }

    // 진입이 가능한 상태인지 조회하는 API
    public Mono<Boolean> isAllowed(final String queue, final Long userId) {
        return reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_PROCEED_KEY.formatted(queue), userId.toString())
                .defaultIfEmpty(-1L) // rank값이 없으면 -1 리턴
                .map(rank -> rank >= 0);
    }

    // 진입을 허용하는 API
    public Mono<Long> allowUser(final String queue, final Long count) { // count : 몇 명의 사용자를 허용할지
        // 진입을 허용하는 단계
        // 1. wait queue 에서 사용자를 제거
        // 2. proceed queue 에 사용자를 추가 (5개의 요청이 오더라도, 3개만 통과되어 3만 리턴될 수 있음)
        return reactiveRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY.formatted(queue), count) // 해당 큐에서 count 값이 작은 유저들을 pop
                .flatMap(member -> reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_PROCEED_KEY.formatted(queue), member.getValue(), Instant.now().getEpochSecond())) // 언제 허용되었는지 타임스탬프 값을 새로 넣어줌
                .count(); // flatMap을 통과한, 허용된 개수를 리턴
    }

    // 대기 순번 리턴 API
    public Mono<Long> getRank(final String queue, final Long userId) {
        return reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString())
                .defaultIfEmpty(-1L)
                .map(rank -> rank >= 0 ? rank + 1 : rank);
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 10000) // 애플리케이션 시작 후 5초 후에 설정된 시간 주기(10초)로 아래 메서드를 실행한다.
    public void scheduleAllowUser() {
        if (!scheduling) {
            log.info("passed Scheduling");
            return;
        }

        log.info("called Scheduling");

        var maxAllowUserCount = 3L; // 최대 3명까지 가능하게 설정
        reactiveRedisTemplate.scan(ScanOptions.scanOptions()
                .match(USER_QUEUE_WAIT_KEY_FOR_SCAN) // 운영 중인 큐들을 모두 찾는데
                .count(100) // 최대 100개까지만 큐를 찾음
                .build())
                .map(key -> key.split(":")[2]) // 큐 이름 가져옴
                .flatMap(queue -> allowUser(queue, maxAllowUserCount).map(allowed -> Tuples.of(queue, allowed))) // 3명을 주기적으로 허용하겠다.
                .doOnNext(tuple -> log.info("Tried %d and allowed %d members of %s queue".formatted(maxAllowUserCount, tuple.getT2(), tuple.getT1()))) // 로그 출력
                .subscribe();
    }

}
