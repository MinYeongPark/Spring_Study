package com.example.cache.domain.repository;

import com.example.cache.domain.entity.RedisHashUser;
import org.springframework.data.repository.CrudRepository;

public interface RedisHashUserRepository extends CrudRepository<RedisHashUser, Long> {
    // 이 RedisHash 관련은 CrudRepository 밖에 상속받을 수 없다!
}
