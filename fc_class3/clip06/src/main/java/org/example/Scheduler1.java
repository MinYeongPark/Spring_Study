package org.example;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Scheduler1 {

    public Flux<Integer> fluxMapWithSubscribeOn() {
        return Flux.range(1, 10)
                .map(i -> i * 2)
                .subscribeOn(Schedulers.boundedElastic()) // subscribeOn 을 하게 되면 해당 stream을 subscribe하는 과정에서 boundedElastic을 전반적으로 사용하겠다는 의미 -> 일련의 과정을 싹 다 boundedElastic으로 쓰겠다는 의미
                .log();
    }

    public Flux<Integer> fluxMapWithPublishOn() {
        return Flux.range(1, 10)
                .map(i -> i + 1)
                .publishOn(Schedulers.boundedElastic()) // 이 메서드를 실행한 다음 구문부터 boundedElastic을 쓰겠다는 의미
                .log()
                .publishOn(Schedulers.parallel())
                .log()
                .map(i -> i * 2);
    }
}
