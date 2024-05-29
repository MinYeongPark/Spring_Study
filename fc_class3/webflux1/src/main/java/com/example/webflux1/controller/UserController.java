package com.example.webflux1.controller;

import com.example.webflux1.dto.UserCreateRequest;
import com.example.webflux1.dto.UserResponse;
import com.example.webflux1.dto.UserUpdateRequest;
import com.example.webflux1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public Mono<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        return userService.create(request.getName(), request.getEmail())
                .map(UserResponse::of);
    }

    @GetMapping("")
    public Flux<UserResponse> findAllUsers() {
        return userService.findAll()
                .map(UserResponse::of); // 1:1로 UserResponse로 매핑
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>>  findUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(UserResponse.of(u))) // 값이 존재하면 UserResponse의 of 메서드 타고,
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())); // 값이 없으면 여기로 와서 not found 리턴!;
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable Long id) {
        return userService.deleteById(id).then(
                Mono.just(ResponseEntity.noContent().build()) // 204(no content)로 전달
        );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        // user가 없으면 : 404 not found
        // user가 있으면 : 200 ok
        // empty를 처리하는 2가지 방법 -> switchIfEmpty, defaultIfEmpty로 if문 처리 가능! & filter를 통해 값이 있고 없고 필터링 처리 가능
        return userService.update(id, request.getName(), request.getEmail())
                .map(u -> ResponseEntity.ok(UserResponse.of(u))) // 값이 존재하면 UserResponse의 of 메서드 타고,
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())); // 값이 없으면 여기로 와서 not found 리턴!
    }
}
