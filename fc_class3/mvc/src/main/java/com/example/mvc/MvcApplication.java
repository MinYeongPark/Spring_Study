package com.example.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SpringBootApplication
public class MvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcApplication.class, args);
	}

	@GetMapping("/posts/{id}")
	public Map<String, String> getPosts(@PathVariable Long id) throws Exception { // 임의로 리턴값을 주는 api
		Thread.sleep(300);

		if (id > 10L) {
			throw new Exception("Too long");
		}

		return Map.of("id", id.toString(),
				"content", "Post content is %d".formatted(id));
	}

}
