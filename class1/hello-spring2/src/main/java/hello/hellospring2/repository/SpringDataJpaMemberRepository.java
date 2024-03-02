package hello.hellospring2.repository;

import hello.hellospring2.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository { // Member의 id는 Long타입
    // JpaRepository를 상속받으면, 구현체를 자동으로 만들어주고 스프링빈에 자동으로 등록해줌. 우리는 그걸 가져다 쓰면 됨.

    // 공통으로 할 수 없는 것은 여기서 따로 우리가 메서드 설정해줘야 함
    // JPQL ->  select m from Member m where m.name = ? -> 인터페이스 이름 개발 만으로도 개발이 완료됨.
    @Override
    Optional<Member> findByName(String name); // 이렇게 하면 다 만든 것임
}
