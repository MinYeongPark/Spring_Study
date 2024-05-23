package org.example;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class Operator1 {
    // map
    public Flux<Integer> fluxMap() {
        return Flux.range(1, 5)
                .map(i -> i * 2) // 1:1로 전환
                .log();
    }

    // filter
    public Flux<Integer> fluxFilter() {
        return Flux.range(1, 10)
                .filter(i -> i > 5) // 위에서 받는 데이터에 대해 이 조건에 일치하는 값만 subscriber S에게 전달
                .log();
    }

    // take
    public Flux<Integer> fluxFilterTake() {
        return Flux.range(1, 10)
                .filter(i -> i > 5) // 위에서 받는 데이터에 대해 이 조건에 일치하는 값만 subscriber S에게 전달
                .take(3) // Stream 데이터에 대해 3개만 취하겠다.
                .log();
    }

    // flatmap
    public Flux<Integer> fluxFlatMap() {
        return Flux.range(1, 10) // 1 ~ 10까지 publish 하는데,
                .flatMap(i -> Flux.range(i * 10, 10)
                        .delayElements(Duration.ofMillis(100))
                ) // i를 받게 되면 그 자리수부터 10개를 뽑아냄 // 1이 오면 10 ~ 19까지 뽑아냄, 2가 오면 20 ~ 29까지 뽑아냄.. // 10 ~ 100까지 동작하는 코드
                .log();
    }

    public Flux<Integer> fluxFlatMap2() {
        // 이런 식으로 이중 for문 구조로 생각해도 좋다.
        // for (int i = 0; ...)
        //    for (int j = 0; ...)

        return Flux.range(1, 9)
                .flatMap(i -> Flux.range(1, 9)
                        .map(j -> {
                            System.out.printf("%d * %d = %d\n", i, j, i * j);
                            return i * j;
                        }) // 1,1 - 1,2 - 1,3 .. 이렇게 전달받는 코드
                )
                .log();
    }
}
