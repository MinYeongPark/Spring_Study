package com.example.webflux1.service;

import com.example.webflux1.client.PostClient;
import com.example.webflux1.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    // webClient를 사용해서 mvc 기반의 server에 request함
    private final PostClient postClient;

    public Mono<PostResponse> getPostContent(Long id) {
        return postClient.getPost(id)
                .onErrorResume(error -> Mono.just(new PostResponse(id.toString(), "Fallback data %d".formatted(id))));
    }

    public Flux<PostResponse> getMultiplePostContent(List<Long> idList) {
        return Flux.fromIterable(idList) // idList에 있는 것을 기반으로 Flux를 새롭게 만들 수 있음
                .flatMap(this::getPostContent)
                .log(); // 각각에 대해 getPostContent에 전달하고 비동기처리함
    }

    public Flux<PostResponse> getParallelMultiplePostContent(List<Long> idList) {
        return Flux.fromIterable(idList) // idList에 있는 것을 기반으로 Flux를 새롭게 만들 수 있음
                .parallel() // 병렬로 요청을 실행
                .runOn(Schedulers.parallel()) // Reactor Schedulers에서 병렬 관련 부분을 활용해서 진행할 것이다.
                .flatMap(this::getPostContent) // 각각에 대해 getPostContent에 전달하고 비동기처리함
                .log()
                .sequential(); // sequential하게 모아서 응답
    }
}
