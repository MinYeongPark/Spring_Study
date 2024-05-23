package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class Operator2 {
    // concatMap
    public Flux<Integer> fluxConcatMap() {
        return Flux.range(1, 10)
                .concatMap(i -> Flux.range(i * 10, 10)
                        .delayElements(Duration.ofMillis(100))
                )
                .log();
    }

    // flatMapMany -> mono to flux
    public Flux<Integer> monoFlatMapMany() {
        return Mono.just(10) // 값 1개 지정
                .flatMapMany(i -> Flux.range(1, i))
                .log(); // 전달받은 값까지 range로 flux를 만드는(변환되는) 작업

    }

    // defaultIfEmpty
    public Mono<Integer> defaultIfEmpty1() {
        return Mono.just(100)
                .filter(i -> i > 100)        // 이렇게 설정하면 전달되는 값이 없음!
                .defaultIfEmpty(30); // 값이 없을 경우 이 값을 대체로 쓰겠다. 값이 있을 경우 그 값을 쓴다.
    }

    // switchIfEmpty
    public Mono<Integer> switchIfEmpty1() {
        return Mono.just(100)
                .filter(i -> i > 100)        // 이렇게 설정하면 전달되는 값이 없음!
                .switchIfEmpty(Mono.just(30).map(i -> i * 2)); // 대안이 되는 것을 만들 수 있음.
    }

    public Mono<Integer> switchIfEmpty2() {
        return Mono.just(100)
                .filter(i -> i > 100)        // 이렇게 설정하면 전달되는 값이 없음!
                .switchIfEmpty(Mono.error(new Exception("Not exists value...")))  // 오류를 반환할 수 있음
                .log();
    }

    // merge, zip
    public Flux<String> fluxMerge() {
        return Flux.merge(Flux.fromIterable(List.of("1", "2", "3")), Flux.just("4")) // 1, 2, 3, + 4
                .log();
    }

    public Flux<String> monoMerge() { // Mono 의 경우 합치게 되면 더이상 단일 값이 아니기 때문에, Flux 반환타입이어야 한다.
        return Mono.just("1")
                .mergeWith(Mono.just("2"))
                .mergeWith(Mono.just("3"))
                .log();
    }

    public Flux<String> fluxZip() {
        return Flux.zip(Flux.just("a","b","c"), Flux.just("d","e","f")) // zip은 각각의 인덱스에 맞는 걸 합칠 수 있게(연산할 수 있게, 묶을 수 있게) 도와줌 // (a-d), (b-e), (c-f)
                .map(i -> i.getT1() + i.getT2()) // ex) getT1 = "a", getT2 = "d" 이 두개를 합쳐서 Stream 데이터로 전달함
                .log();
    }

    public Mono<Integer> monoZip() {
        return Mono.zip(Mono.just(1), Mono.just(2), Mono.just(3)) // Mono는 하나이기 때문에 그냥 mono 전체를 합친다고 봐도 무방하다.
                .map(i -> i.getT1() + i.getT2() + i.getT3()); // 인자가 3개여서 T3까지 있음!
    }
}
