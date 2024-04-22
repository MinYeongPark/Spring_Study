package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;

    protected Member() { // JPA는 기본 생성자가 있어야 한다. // 프록시 기술 등등 쓸 때 private으로 하면 막히기 때문에 열어줘야 한다.
    }

    public Member(String username) {
        this.username = username;
    }
}
