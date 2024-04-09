package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        try (var jedisPool = new JedisPool("127.0.0.1", 6379)) {
            try (Jedis jedis = jedisPool.getResource()) {
                // hset
                jedis.hset("users:2:info", "name", "greg2");

                var userInfo = new HashMap<String, String>();
                userInfo.put("email", "greg2@gmail.com");
                userInfo.put("phone", "010-0000-0000");
                jedis.hset("users:2:info", userInfo);

                // hdel
                jedis.hdel("users:2:info", "phone");

                // get, getall
                System.out.println(jedis.hget("users:2:info", "email"));
                Map<String, String> user2Info = jedis.hgetAll("users:2:info");
                user2Info.forEach((k, v) -> System.out.printf("%s %s%n", k, v));

                // hincr
                jedis.hincrBy("users:2:info", "visits", 30);

            }
        }
    }
}