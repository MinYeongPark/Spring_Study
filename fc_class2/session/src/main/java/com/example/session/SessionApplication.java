package com.example.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SpringBootApplication
public class SessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionApplication.class, args);
	}

	@GetMapping("/")
	public Map<String, String> home(HttpSession session) {
		Integer visitCount = (Integer) session.getAttribute("visits"); // visits 값을 가져옴
		if (visitCount == null) { // 예외 처리
			visitCount = 0;
		}

		session.setAttribute("visits", ++visitCount); // visitCount 늘려주고 세션에 저장
		return Map.of("session id", session.getId(), "visits", visitCount.toString());
	}
}
