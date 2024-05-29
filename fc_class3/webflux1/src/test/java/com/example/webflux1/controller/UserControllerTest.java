package com.example.webflux1.controller;

import com.example.webflux1.dto.UserCreateRequest;
import com.example.webflux1.dto.UserResponse;
import com.example.webflux1.repository.User;
import com.example.webflux1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@AutoConfigureWebTestClient
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient; // 테스트용 웹 클라이언트

    @MockBean
    private UserService userService; // userController에서 가짜 userService를 활용하게 하기 위함!

    @Test
    void createUser() {
        when(userService.create("min", "min@naver.com")).thenReturn(
                        Mono.just(new User(1L, "min", "min@naver.com", LocalDateTime.now(), LocalDateTime.now()))
                );

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateRequest("min", "min@naver.com"))
                .exchange() // 요청됨
                .expectStatus().is2xxSuccessful()
                .expectBody(UserResponse.class)
                .value(res -> {
                    assertEquals("min", res.getName());
                    assertEquals("min@naver.com", res.getEmail());
                });
    }

    @Test
    void findAllUsers() {
        when(userService.findAll()).thenReturn(
                Flux.just(
                        new User(1L, "min", "min@naver.com", LocalDateTime.now(), LocalDateTime.now()),
                        new User(2L, "min", "min@naver.com", LocalDateTime.now(), LocalDateTime.now()),
                        new User(3L, "min", "min@naver.com", LocalDateTime.now(), LocalDateTime.now())
                ));

        webTestClient.get().uri("/users")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(UserResponse.class)
                .hasSize(3); // 3개의 사이즈를 응답받을 것이다.
    }

    @Test
    void findUser() {

        when(userService.findById(1L)).thenReturn(
                Mono.just(new User(1L, "min", "min@naver.com", LocalDateTime.now(), LocalDateTime.now()))
        );

        webTestClient.get().uri("/users/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserResponse.class)
                .value(res -> {
                    assertEquals("min", res.getName());
                    assertEquals("min@naver.com", res.getEmail());
                });
    }

    @Test
    void notFoundUser() {

        when(userService.findById(1L)).thenReturn(
                Mono.empty() // 값이 없는 경우
        );

        webTestClient.get().uri("/users/1")
                .exchange()
                .expectStatus().is4xxClientError(); // 값이 없어서 400대 에러
    }

    @Test
    void deleteUser() {
        when(userService.deleteById(1L)).thenReturn(
                Mono.just(1) // 값이 잘 삭제된 경우 Mono 1 리턴됨
        );


        webTestClient.delete().uri("/users/1")
                .exchange()
                .expectStatus().is2xxSuccessful(); // 204 반환될 것임
    }

    @Test
    void updateUser() {
        when(userService.update(1L, "min11", "min11@naver.com")).thenReturn(
                Mono.just(new User(1L, "min11", "min11@naver.com", LocalDateTime.now(), LocalDateTime.now()))
        );

        webTestClient.put().uri("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateRequest("min11", "min11@naver.com"))
                .exchange() // 요청됨
                .expectStatus().is2xxSuccessful()
                .expectBody(UserResponse.class)
                .value(res -> {
                    assertEquals("min11", res.getName());
                    assertEquals("min11@naver.com", res.getEmail());
                });
    }
}