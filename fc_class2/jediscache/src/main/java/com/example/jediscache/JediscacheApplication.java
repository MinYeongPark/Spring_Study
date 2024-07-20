package com.example.jediscache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class JediscacheApplication implements ApplicationRunner {

	private final UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(JediscacheApplication.class, args);
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		userRepository.save(User.builder().name("a111").email("111@naver.com").build());
		userRepository.save(User.builder().name("a222").email("222@naver.com").build());
		userRepository.save(User.builder().name("a333").email("333@naver.com").build());
		userRepository.save(User.builder().name("a444").email("444@naver.com").build());
	}
}
