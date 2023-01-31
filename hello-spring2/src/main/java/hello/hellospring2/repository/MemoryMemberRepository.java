package hello.hellospring2.repository;

import hello.hellospring2.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L; // 키 값을 생성해주는 것

    @Override
    public Member save(Member member) {
        member.setId(++sequence); // 아이디는 시스템이 정해줌
        store.put(member.getId(), member); // 저장
        return member; // 저장 결과 반환
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id)); // 감싸서 반환함
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name)) // 이름을 가지고 비교해서 찾음.
                .findAny(); // 하나라도 찾음.
        // 루프를 다 돌면서 하나라도 찾으면 반환하고, 하나도 없으면 Optional에 넣어서 반환함.
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values()); // 멤버들이 반환됨.
    }

    public void clearStore() {
        store.clear(); // 스토어를 싹 비움
    }
}
