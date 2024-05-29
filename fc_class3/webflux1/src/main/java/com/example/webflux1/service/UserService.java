package com.example.webflux1.service;

import com.example.webflux1.repository.User;
import com.example.webflux1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // create, update, delete, read

    public Mono<User> create(String name, String email) {
        return userRepository.save(User.builder().name(name).email(email).build());
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<Integer> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    public Mono<User> update(Long id, String name, String email) {
        // 1) 해당 사용자를 찾는다.
        // 2) 데이터를 변경하고 저장한다.
        return userRepository.findById(id)
                .flatMap(u -> {       // flatMap : 1:N 으로, 찾아낸 user에 대해 작업을 진행
                    u.setName(name);  // 값을 세팅
                    u.setEmail(email);
                    return userRepository.save(u); // Mono<User> 리턴됨
                });
    }
}
