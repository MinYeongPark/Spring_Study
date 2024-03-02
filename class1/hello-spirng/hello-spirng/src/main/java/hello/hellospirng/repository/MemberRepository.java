package hello.hellospirng.repository;

import hello.hellospirng.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id); // Optional : null 대신 Optional로 감싸서 하는 것
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
