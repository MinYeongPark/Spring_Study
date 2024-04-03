package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        try (var jedisPool = new JedisPool("127.0.0.1", 6379)) {
            try (Jedis jedis = jedisPool.getResource()) {

//                jedis.set("users:300:email", "aa@gmail.com");
//                jedis.set("users:300:name", "min 00");
//                jedis.set("users:300:age", "100");
//
//                String userEmail = jedis.get("users:300:email");
//                System.out.println("userEmail = " + userEmail);
//
//                List<String> userInfo = jedis.mget("users:300:email", "users:300:name", "users:300:age");
//                userInfo.forEach(System.out::println);
//
//                long counter = jedis.incr("counter");
//                System.out.println(counter);
//
//                counter = jedis.incrBy("counter", 10L);
//                System.out.println("counter = " + counter);
//
//                counter = jedis.decr("counter");
//                System.out.println("counter = " + counter);
//
//                counter = jedis.decrBy("counter", 20L);
//                System.out.println("counter = " + counter);

                // 파이프라인 실습
                Pipeline pipelined = jedis.pipelined();
                pipelined.set("users:400:email", "aaa@naver.ocm");
                pipelined.set("users:400:name", "yeong");
                pipelined.set("users:400:age", "10");
                List<Object> objects = pipelined.syncAndReturnAll();// 3가지를 묶어서 하나의 요청으로 보내게 됨!
                objects.forEach(i -> System.out.println(i.toString()));

            }
        }
    }
}