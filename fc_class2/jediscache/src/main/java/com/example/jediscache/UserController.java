package com.example.jediscache;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JedisPool jedisPool;

    @GetMapping("/users/{id}/email")
    public String getUserEmail(@PathVariable Long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            var userEmailRedisKey = "users:%d:email".formatted(id);

            // 1. request to cache
            // "users:id:email" 로 키를 정하겠다.
            String userEmail = jedis.get(userEmailRedisKey);
            if (userEmail != null) {
                return userEmail;
            }

            // 2. else to db
            userEmail = userRepository.findById(id)
                    .orElse(User.builder().build()) // 값이 없는 경우 새로 만들어줌
                    .getEmail();

            // 3. cache에 저장
            jedis.set(userEmailRedisKey, userEmail);

            // TTL 값 저장
            jedis.setex(userEmailRedisKey, 30, userEmail);

            return userEmail;
        }
    }
}
