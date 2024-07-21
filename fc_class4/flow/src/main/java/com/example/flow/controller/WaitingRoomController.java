package com.example.flow.controller;

import com.example.flow.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class WaitingRoomController {

    private final UserQueueService userQueueService;

    @GetMapping("/waiting-room")
    Mono<Rendering> waitingRoomPage(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                    @RequestParam(name = "user_id") Long userId,
                                    @RequestParam(name = "redirect_url") String redirectUrl,
                                    ServerWebExchange exchange) {

        var key = "user-queue-%s-token".formatted(queue);
        var cookieValue = exchange.getRequest().getCookies().getFirst(key);
        var token = (cookieValue == null) ? "" : cookieValue.getValue();

        return userQueueService.isAllowedByToken(queue, userId, token) // 1. 입장이 허용되어 page redirect(이동)이 가능한 상태인가?
                .filter(allowed -> allowed)
                .flatMap(allowed -> Mono.just(Rendering.redirectTo(redirectUrl).build())) // 2. 어디로 이동해야 하는가?
                .switchIfEmpty(
                        // 대기 등록
                        // 웹페이지에 필요한 데이터를 전달
                        userQueueService.registerWaitQueue(queue, userId)
                                .onErrorResume(ex -> userQueueService.getRank(queue, userId)) // 이미 등록되어 있는 경우, 대기 번호를 보여줌
                                .map(rank -> Rendering.view("waiting-room.html")
                                        .modelAttribute("number", rank) // 웹페이지에 필요한 데이터를 전달
                                        .modelAttribute("userId", userId)
                                        .modelAttribute("queue", queue)
                                        .build())
                );
    }
}
