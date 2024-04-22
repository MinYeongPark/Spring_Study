package org.example;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        try(var jedisPool = new JedisPool("127.0.0.1", 6379)) {
            try (var jedis = jedisPool.getResource()) {
                jedis.setbit("request-somepage-20240408", 100, true);
                jedis.setbit("request-somepage-20240408", 200, true);
                jedis.setbit("request-somepage-20240408", 300, true);

                System.out.println(jedis.getbit("request-somepage-20240408", 100));
                System.out.println(jedis.getbit("request-somepage-20240408", 50));

                System.out.println(jedis.bitcount("request-somepage-20240408"));

                // bitmap vs set
                Pipeline pipelined = jedis.pipelined();
                IntStream.rangeClosed(0, 100000).forEach(i -> {
                    pipelined.sadd("request-somepage-set-20240409", String.valueOf(i), "1");
                    pipelined.setbit("request-somepage-bit-20240408", i, true);

                    if (i == 1000) { // 1000개 단위로 (2000개의 명령) 요청 보냄
                        pipelined.sync();
                    }
                });
                pipelined.sync();
            }
        }
    }
}