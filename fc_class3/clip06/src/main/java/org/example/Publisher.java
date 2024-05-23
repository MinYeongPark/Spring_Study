package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class Publisher {

    public Flux<Integer> startFlux() {
        return Flux.range(1, 10).log();
    }

    public Flux<String> startFlux2() {
        return Flux.fromIterable(List.of("a", "b", "c", "d")).log();
    }

    public Mono<Integer> startMono() {
        return Mono.just(1).log(); // 1을 리턴함
    }

    public Mono<?> startMono2() { // Mono는 아무것도 없는 값을 전달할 수 있다.
        return Mono.empty().log(); // next 데이터를 전달하지 않음
    }

    public Mono<?> startMono3() { // Mono는 에러 처리에도 적합하다.
        return Mono.error(new Exception("hello reactor")).log();
    }
}
