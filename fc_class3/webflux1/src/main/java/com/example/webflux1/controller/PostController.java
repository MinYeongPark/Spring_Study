package com.example.webflux1.controller;

import com.example.webflux1.dto.PostResponse;
import com.example.webflux1.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    public Mono<PostResponse> getPostContent(@PathVariable Long id) {
        return postService.getPostContent(id);
    }

    @GetMapping("/search")
    public Flux<PostResponse> getMultiplePostContent(@RequestParam(name = "ids") List<Long> idList) {
       // 여기에서는 여러 값을 받을 것이므로 Flux 타입으로 받음
//        return postService.getMultiplePostContent(idList);
        return postService.getParallelMultiplePostContent(idList);
    }


}
