package org.example;

public class Main {
    public static void main(String[] args) {
        var publisher = new Publisher();

        // Flux
//        publisher.startFlux()
//                .subscribe(System.out::println); // 꼭 subscribe 해주어야 동작함!

        // Mono
        publisher.startMono2()
                .subscribe(); // 여기에서는 출력은 따로 안 하고 로그만 확인하려고 함!
    }
}