package com.example.webflux1.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {

    // User 객체를 저장해야 하는데,
    // key가 id이고, 그 키에 대해 User 정보를 저장할 수 있는 자료구조가 필요하다.
    // 이에 가장 적절한 것은, HashMap인데, ConcurrentHashMap을 이용해 작업해볼 것이다.
    private final ConcurrentHashMap<Long, User> userHashMap = new ConcurrentHashMap<>();
    private AtomicLong sequence = new AtomicLong(1L); // 동시다발적인 save 상황에서 원자적으로 값을 다루기 위해 atomic~~ 사용

    @Override
    public Mono<User> save(User user) {
        // created_at, updated_at
        var now = LocalDateTime.now();

        // create, update 로직
        // create의 경우에는 id를 빈 값을 넣어주면 알아서 채워주는 식으로 진행
        if (user.getId() == null) {
            user.setId(sequence.getAndAdd(1)); // 값 가져온 후 1 증가
            user.setCreatedAt(now);
        }
        user.setUpdatedAt(now); // update 로직은 여기에서부터 진행
        userHashMap.put(user.getId(), user); // 실은 hashmap에서 put은 아예 몽땅 교체하는 것이긴 함!
        return Mono.just(user);
    }

    @Override
    public Flux<User> findAll() {
        // ConcurrentHashMap에 저장된 값들을 모두 꺼내서 리턴하면 됨
        return Flux.fromIterable(userHashMap.values());
    }

    @Override
    public Mono<User> findById(Long id) {
        return Mono.justOrEmpty(userHashMap.getOrDefault(id, null)); // null을 값으로 받으면 값이 없는 Mono
    }

    @Override
    public Mono<Integer> deleteById(Long id) {
        User user = userHashMap.getOrDefault(id, null);
        if (user == null) {
            return Mono.just(0); // 기본 값 리턴
        }
        userHashMap.remove(id, user);
        return Mono.just(1); // 삭제했다는 의미
    }
}
