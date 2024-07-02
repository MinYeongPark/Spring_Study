package com.example.webflux1.client;

import com.example.webflux1.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PostClient {
    private final WebClient webClient;
    private final String url = "http://127.0.0.1:8090";

    // Spring Webflux에서 WebClient를 사용해서 -> mvc 서버에 ("/posts/{id}") path로 웹 요청을 하는 코드
    public Mono<PostResponse> getPost(Long id) {
        String uriString = UriComponentsBuilder.fromHttpUrl(url)
                .path("/posts/%d".formatted(id))
                .buildAndExpand()
                .toUriString();

        return webClient.get() // 메서드를 먼저 선택 -> 여기에서는 get 메서드로 받겠다고 설정
                .uri(uriString)
                .retrieve() // 해당 메서드, uri를 호출하면 응답값 받음
                .bodyToMono(PostResponse.class); // 응답값을 mono로 받을 수 있다.
    }
}