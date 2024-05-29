package com.example.webflux1.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest { // RepositoryTest는 클라이언트를 통해 테스트가 아닌, 이 repository 자체의 동작을 검증하는 것이다.

    private final UserRepository userRepository = new UserRepositoryImpl();

    @Test
    void save() {
        var user = User.builder().name("min").email("min@naver.com").build();
        StepVerifier.create(userRepository.save(user))
                .assertNext(u -> {
                    assertEquals(1L, u.getId());
                    assertEquals("min", u.getName());
                    assertEquals("min@naver.com", u.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void findAll() {
        userRepository.save(User.builder().name("min").email("min@naver.com").build());
        userRepository.save(User.builder().name("min2").email("min2@naver.com").build());
        userRepository.save(User.builder().name("min3").email("min3@naver.com").build());

        StepVerifier.create(userRepository.findAll())
                .expectNextCount(3) // 3개 값 반환됨
                .verifyComplete();
    }

    @Test
    void findById() {
        userRepository.save(User.builder().name("min").email("min@naver.com").build());
        userRepository.save(User.builder().name("min2").email("min2@naver.com").build());

        StepVerifier.create(userRepository.findById(2L))
                .assertNext(u -> {
                    assertEquals(2L, u.getId());
                    assertEquals("min2", u.getName());
                })
                .verifyComplete();
    }

    @Test
    void deleteById() {
        userRepository.save(User.builder().name("min").email("min@naver.com").build());
        userRepository.save(User.builder().name("min2").email("min2@naver.com").build());

        StepVerifier.create(userRepository.deleteById(2L))
                .expectNextCount(1) // 2개 중 1개 삭제해서 1개만 리턴되길 기대함!
                .verifyComplete();
    }
}