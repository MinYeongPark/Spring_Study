package com.example.webflux1.controller;

import com.example.webflux1.dto.PostCreateRequest;
import com.example.webflux1.dto.PostResponseV2;
import com.example.webflux1.service.PostServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/posts")
public class PostControllerV2 {
    private final PostServiceV2 postServiceV2;

    @PostMapping("")
    public Mono<PostResponseV2> createPost(@RequestBody PostCreateRequest request) {
        return postServiceV2.create(request.getUserId(), request.getTitle(), request.getContent())
                .map(PostResponseV2::of);
    }

    @GetMapping("")
    public Flux<PostResponseV2> findAllPost() {
        return postServiceV2.findAll()
                .map(PostResponseV2::of);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostResponseV2>> findPost(@PathVariable Long id) {
        return postServiceV2.findById(id)
                .map(p -> ResponseEntity.ok().body(PostResponseV2.of(p))) // 만약 조회되는 게 있으면 ok로 응답하고 postResponseV2에 담아서 응답
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())); // 조회되는 게 없으면 위에서 map되는 데이터가 없기 때문에 여기까지 내려오게 되고, notFound로 응답
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deletePost(@PathVariable Long id) {
        return postServiceV2.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}