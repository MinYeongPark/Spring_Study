package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class Operator3 {
    // count
    // 송출되는 element의 개수를 합쳐서 단일 값으로 전달
    public Mono<Long> fluxCount() {
        return Flux.range(1, 10)
                .count().log(); // 1 ~ 10까지 publish 될텐데, 개수를 세어줌
    }

    // distinct
    // 중복 값을 찾아서 없애줌
    public Flux<String> fluxDistinct() {
        return Flux.fromIterable(List.of("a", "b", "a", "b", "c")) // 여기 중에서 중복값 제거해서 a,b,c만 들어올 것임
                .distinct().log();
    }

    // reduce
    // 연산하기 좋은 옵션

    // 아래 예제) 연속된 숫자가 있다면
    // 1,2,3,4,5   >> 1+2 더함
    // -> 3,3,4,5  >> 3+3 더함
    // -> 6,4,5    >> 6+4 더함
    // -> 10,5     >> 10+5 더함
    // -> 15
    public Mono<Integer> fluxReduce() {
       return Flux.range(1, 10)
                .reduce((i, j) -> i + j)
                .log();
    }

    // groupby
    // 동일한 값에 대해 묶어서 처리 가능
    public Flux<Integer> fluxGroupBy() {
        return Flux.range(1, 10)
                .groupBy(i -> (i % 2 == 0) ? "even" : "odd") // 짝수 홀수 판별 -> groupedFlux한 값을 가지게 됨
                .flatMap(group -> group.reduce((i, j) -> i + j)) // 홀수 짝수가 나뉘어져 있고, 이를 각 그룹별로 더해서 전달하는 연산
                .log();
    }

}
