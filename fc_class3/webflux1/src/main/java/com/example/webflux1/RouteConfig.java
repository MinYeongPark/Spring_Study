package com.example.webflux1;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final SampleHandler sampleHandler;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route()
                .GET("/hello-functional", sampleHandler::getString) // http 메서드 설정을 할 수 있는데, path 뒤에 매핑되는 핸들러를 설정할 수 있다.
                .build();
    }
}
