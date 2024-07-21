package com.example.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.example.flow.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserQueueService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final String USER_QUEUE_WAIT_KEY = "user:queue:%s:wait"; // redis는 관용적으로 key 이름에 : 을 많이 씀
                                                                     // %s : 필요 시 큐를 여러개 쓸 수 있도록 가변적으로 만듦

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
}
