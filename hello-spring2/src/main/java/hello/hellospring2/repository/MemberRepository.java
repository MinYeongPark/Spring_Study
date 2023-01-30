package hello.hellospring2.repository;

import hello.hellospring2.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id); // Optional : Null을 Optional로 감싸서 반환 가능
    Optional<Member> findByName(String name);
    List<Member> findAll(); // 모든 회원 리스트 반환
}
