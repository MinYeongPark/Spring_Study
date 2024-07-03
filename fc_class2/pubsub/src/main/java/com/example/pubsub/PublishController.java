package com.example.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublishController {

    private final RedisTemplate<String, String> redisTemplate; // Spring data redis 에서는 redisTemplate을 쓴다.

    @PostMapping("events/users/deregister")
    void publishDeregisterEvent() {
        redisTemplate.convertAndSend("users:unregister", "500"); // 메시지 발행
    }
}