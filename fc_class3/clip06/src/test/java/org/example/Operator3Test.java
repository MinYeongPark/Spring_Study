package org.example;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class Operator3Test {

    private Operator3 operator3 = new Operator3();

    @Test
    void fluxCount() {
        StepVerifier.create(operator3.fluxCount())
                .expectNext(10L) // 10개니까 10개 기대함  // Long 타입이어서 L 붙임
                .verifyComplete();
    }

    @Test
    void fluxDistinct() {
        StepVerifier.create(operator3.fluxDistinct())
                .expectNext("a", "b", "c")
                .verifyComplete();
    }

    @Test
    void fluxReduce() {
        StepVerifier.create(operator3.fluxReduce())
                .expectNext(55)
                .verifyComplete();
    }

    @Test
    void fluxGroupBy() {
        StepVerifier.create(operator3.fluxGroupBy())
                .expectNext(30) // 짝수의 합
                .expectNext(25) // 홀수의 합
                .verifyComplete();
    }
}