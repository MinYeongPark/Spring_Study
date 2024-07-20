package com.example.mvc;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@SpringBootApplication
public class MvcApplication implements ApplicationListener<ApplicationReadyEvent> {

	private final RedisTemplate<String, String> redisTemplate; // key-value가 String-String인 redisTemplate을 주입받음
	private final UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(MvcApplication.class, args);
	}

	// 바로 응답할 수 있는 API - 외부 요청 없이 바로 응답 줌
	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("health", "ok");
	}

	// Redis 캐시 요청하는 API
	@GetMapping("/users/1/cache")
	public Map<String, String> getCachedUser() { // 캐시의 경우, 캐시에서 읽어옴
		var name = redisTemplate.opsForValue().get("users:1:name");
		var email = redisTemplate.opsForValue().get("users:1:email");

		return Map.of("name", name == null ? "" : name, // 널이면 빈 문자열 리턴
				"email", email == null ? "" : email);
	}

	// RDB에 요청하는 API
	@GetMapping("/users/{id}")
	public User getUser(@PathVariable Long id) {
		return userRepository.findById(id).orElse(new User()); // DB에서 가져왔을 때 없는 경우에는 빈 값으로 처리(new User())
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) { // 값이 없는 경우를 대비해 초기 세팅 진행
		redisTemplate.opsForValue().set("users:1:name", "min11");
		redisTemplate.opsForValue().set("users:1:email", "min11@gmail.com");

		Optional<User> user = userRepository.findById(1L);
		if (user.isEmpty()) {
			userRepository.save(User.builder().name("min11")
					.email("min11@gmail.com").build());
		}
	}
}

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@DynamicInsert // insert 구문이 발생할때 변경된 필드만 insert 쿼리가 작성되게 한다. 그 외에 변경이 되지 않는 필드들은 Default값 or Null 값으로 채워짐
@DynamicUpdate
@Table(name = "users")
class User {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	private String name;
	private String email;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

interface UserRepository extends JpaRepository<User, Long> {
}