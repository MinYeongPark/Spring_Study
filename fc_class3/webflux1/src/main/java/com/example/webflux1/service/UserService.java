package com.example.webflux1.service;

import com.example.webflux1.repository.User;
import com.example.webflux1.repository.UserR2dbcRepository;
import com.example.webflux1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {
//    private final UserRepository userRepository;
    private final UserR2dbcRepository userR2dbcRepository;
    private final ReactiveRedisTemplate<String, User> reactiveRedisTemplate;

    // create, update, delete, read

    public Mono<User> create(String name, String email) {
        return userR2dbcRepository.save(User.builder().name(name).email(email).build());
    }

    public Flux<User> findAll() {
        return userR2dbcRepository.findAll();
    }

    private String getUserCacheKey(Long id) {
        return "users:%d".formatted(id);
    }

    public Mono<User> findById(Long id) {
        // 1. redis 조회
        // 2. 값이 존재하면 응답을 하고
        // 3. 만약 없으면 DB에 질의하고 그 결과를 redis에 저장하는 흐름

        return reactiveRedisTemplate.opsForValue()
                .get(getUserCacheKey(id)) // redis에 저장된 키 값이 있는지 확인 -> 만약 값이 있으면 그 값 가지고 바로 리턴!
                .switchIfEmpty( // 만약 없으면,
                        userR2dbcRepository.findById(id) // DB에서 값을 가져온 것에 대해
                                .flatMap(u -> reactiveRedisTemplate.opsForValue() // redis에 값을 세팅하고
                                        .set("users:%d".formatted(id), u, Duration.ofSeconds(30)) // 30초 정도만 유지하도록 설정
                                        .then(Mono.just(u))) // 세팅된 객체 u에 대해 Mono로 감싸서 응답 및 전달
                );

        // 예전 코드
//        return userR2dbcRepository.findById(id);
    }

    public Mono<Void> deleteById(Long id) {
        return userR2dbcRepository.deleteById(id)
                .then(reactiveRedisTemplate.unlink(getUserCacheKey(id))) // 캐시에서도 삭제
                .then(Mono.empty());
    }

    public Mono<Void> deleteByName(String name) {
        return userR2dbcRepository.deleteByName(name);
    }

    public Mono<User> update(Long id, String name, String email) {
        // 1) 해당 사용자를 찾는다.
        // 2) 데이터를 변경하고 저장한다.

        // redis를 적용했을 때,
        // update를 하게 되면 cache에 있는 값이 더이상 유효하지 않기 때문에
        // 캐시를 삭제해주어야 한다.

        return userR2dbcRepository.findById(id)
                .flatMap(u -> {       // flatMap : 1:N 으로, 찾아낸 user에 대해 작업을 진행
                    u.setName(name);  // 값을 세팅
                    u.setEmail(email);
                    return userR2dbcRepository.save(u); // Mono<User> 리턴됨
                }) // 여기까지, DB 업데이트를 먼저 하고,
                .flatMap(u -> reactiveRedisTemplate.unlink(getUserCacheKey(id)).then(Mono.just(u)));
                        // reactiveRedisTemplate이 붙어서 unlink로 해당 내용을 삭제함
                        // unlink는 비동기적으로 키를 삭제하는 명령어 (delete는 동기식으로 삭제)
    }
}
