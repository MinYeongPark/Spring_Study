package org.example;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class Operator4 {
    // limit
    // // 딜레이를 줘서 Flux 데이터를 실행하게 전달함 // back pressure 의 request를 조절함(limit를 강제함)
    public Flux<Integer> fluxDelayAndLimit() {
        return Flux.range(1, 10)
                .delaySequence(Duration.ofSeconds(1)) // 한번 실행한 후 1초씩 딜레이를 줌
                .log()
                .limitRate(2); // 한번에 2개까지 실행시키도록 강제
    }

    // sample
    public Flux<Integer> fluxSample() {
        return Flux.range(1, 100)
                .delayElements(Duration.ofMillis(100)) // 딜레이 주는 이유는, sampling하는 시간을 벌기 위함!
                .sample(Duration.ofMillis(300)) // 시간 300동안 받은 데이터 중에서 일부만 전달하는 것 -> 선별적으로 데이터를 샘플링한다
                .log();
    }
}
