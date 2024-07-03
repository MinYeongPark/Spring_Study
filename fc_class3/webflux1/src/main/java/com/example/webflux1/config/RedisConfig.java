package com.example.webflux1.config;

import com.example.webflux1.repository.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        reactiveRedisTemplate.opsForValue().get("1")
                .doOnSuccess(i -> log.info("Initialize to redis connection"))
                .doOnError((err) -> log.error("Failed to initialize to redis connection: {}", err.getMessage()))
                .subscribe();
    }

    @Bean
    public ReactiveRedisTemplate<String, User> reactiveRedisUserTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        // ReactiveRedisConnectionFactory : SpringDataRedis에서 제공하는 인터페이스 -> reactive 방식으로 redis와 연결을 관리하고 생성
        // 이 인터페이스는 Redis에 비동기적으로 연결하고 명령을 실행할 수 있는 리액티브 연결 팩토리를 정의한다.

        // objectMapper를 생성해서, json 직렬화/역직렬화 시 특정 설정을 적용함
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 모르는 property 가 있을 때에는 fail 처리 x (무시)
                .registerModule(new JavaTimeModule()) // java 8 날짜/시간 모듈을 등록하여 날짜/시간 직렬화/역직렬화를 지원함
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS); // WRITE_DATE_KEYS_AS_TIMESTAMPS 옵션을 비활성화(disable) -> 날짜를 타임스탬프가 아닌 ISO-8601 문자열로 직렬화함(읽기 쉽게 하기 위함)
                                                                                // +) ISO-8601 형식 : 사람이 읽을 수 있는 날짜/시간 형식으로, 일반적으로 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'와 같은 형식. ex) 2021-08-02T10:15:30.000Z와 같은 문자열 형식

        //  Jackson2JsonRedisSerializer를 생성하여
        //  User 객체를 JSON으로 직렬화/역직렬화할 때 사용할 ObjectMapper를 설정합니다.
        Jackson2JsonRedisSerializer<User> jsonSerializer = new Jackson2JsonRedisSerializer<User>(objectMapper, User.class);

        // RedisSerializationContext를 생성하여
        // 키와 값, 해시 키와 해시 값을
        // 각각 직렬화할 방법을 설정합니다.
        RedisSerializationContext<String, User> serializationContext = RedisSerializationContext
                .<String, User>newSerializationContext()
                .key(RedisSerializer.string()) // 키는 문자열로 직렬화
                .value(jsonSerializer)         // 값은 JSON으로 직렬화
                .hashKey(RedisSerializer.string()) // 해시 키는 문자열로 직렬화
                .hashValue(jsonSerializer)         // 해시 값은 JSON으로 직렬화
                .build();

        // 설정된 직렬화 컨텍스트와 연결 팩토리를 사용하여 ReactiveRedisTemplate를 생성하고 반환합니다.
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
