package com.example.webflux1.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableR2dbcRepositories
@EnableR2dbcAuditing // 자동으로 @CreatedDate, @LastModifiedDate 어노테이션 달린 필드에 값을 채워줌
public class R2dbcConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final DatabaseClient databaseClient;

    // 애플리케이션 ready가 될 때에 대한 handler를 단다
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // reactor : publisher, subscriber
        databaseClient.sql("SELECT 1").fetch().one() // 간단한 쿼리 - 1가지 가져옴
                .subscribe( // 해당 databaseClient에 대해 subscribe
                    success -> {
                        log.info("Initialize r2dbc database connection.");
                    },
                        error -> {
                            log.error("Failed to initialize r2dbc database connection.");
                            SpringApplication.exit(event.getApplicationContext(), () -> -110); // 실행 시 오류가 나면(connection이 정상적으로 초기화되지 않으면), 아예 애플리케이션 종료, 응답 코드는 -110 으로 정함
                        }
                );
    }
}
