package com.example.webflux1;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SampleHandler {
    public Mono<ServerResponse> getString(ServerRequest request) { // sampleHandler 에서도 ServerResponse 타입으로 맞춰서 리턴되도록 해야 함
        return ServerResponse.ok().bodyValue("hello, functional endpoint");
    }
}
