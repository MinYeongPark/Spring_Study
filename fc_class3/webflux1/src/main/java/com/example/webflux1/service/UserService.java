package com.example.webflux1.service;

import com.example.webflux1.repository.User;
import com.example.webflux1.repository.UserR2dbcRepository;
import com.example.webflux1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
//    private final UserRepository userRepository;
    private final UserR2dbcRepository userR2dbcRepository;

    // create, update, delete, read

    public Mono<User> create(String name, String email) {
        return userR2dbcRepository.save(User.builder().name(name).email(email).build());
    }

    public Flux<User> findAll() {
        return userR2dbcRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userR2dbcRepository.findById(id);
    }

    public Mono<Void> deleteById(Long id) {
        return userR2dbcRepository.deleteById(id);
    }

    public Mono<Void> deleteByName(String name) {
        return userR2dbcRepository.deleteByName(name);
    }

    public Mono<User> update(Long id, String name, String email) {
        // 1) 해당 사용자를 찾는다.
        // 2) 데이터를 변경하고 저장한다.
        return userR2dbcRepository.findById(id)
                .flatMap(u -> {       // flatMap : 1:N 으로, 찾아낸 user에 대해 작업을 진행
                    u.setName(name);  // 값을 세팅
                    u.setEmail(email);
                    return userR2dbcRepository.save(u); // Mono<User> 리턴됨
                });
    }
}
