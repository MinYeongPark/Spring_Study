package com.example.webflux1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SampleController {

    @GetMapping("/sample/hello")
    public Mono<String> getHello() {

        // reactor 는
        // publisher <-> subscriber 사이의 데이터 교환이라고 볼 수 있다.
        // 이를 통해 event driven 하게 데이터 stream을 처리할 수 있다.

        // Mono를 return한다는 것은, publisher만 있고 subscriber는 없다는 뜻이다.
        // reactor는 subscribe하지 않으면 동작하지 않는 구조를 가지고 있는데,

        // 그래서 아래 코드에서는 구독 자체가 보이지 않는데,
        // 이 내용은 spring webflux에서 이 리턴되는 값들에 대해 별도의 구독을 하고 있다고 보면 된다.

        // 이 controller에서는 publish를 하고
        // publish되는 내용들에 대해서 subscriber에 잘 전달될 수 있도록
        // return 까지 코드를 잘 전달해주어야 하는 것이다.

        // 이 예제는 간단한 예시지만
        // 만약 많은 비즈니스 로직이 들어가게 된다면
        // 체인이 끊기지 않게 잘 전달해주어야 한다.

        return Mono.just("hello rest controller with webflux");
    }
}
