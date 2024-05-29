package com.example.webflux1.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    // Create,Update
    Mono<User> save(User user);

    // READ
    Flux<User> findAll();
    Mono<User> findById(Long id);

    // DELETE
    Mono<Integer> deleteById(Long id);
}
