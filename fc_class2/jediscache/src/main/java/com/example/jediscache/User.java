package com.example.jediscache;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class) // 각 필드에 어노테이션으로 달아준 제약조건들을 자동으로 설정할 수 있는 JPA 속성
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String name;

    @CreatedDate // 데이터가 생성될 때 자동으로 값이 들어감
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 업데이트되면 자동으로 값이 갱신됨
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
