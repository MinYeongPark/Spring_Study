package hello.hellospirng.repository;

import hello.hellospirng.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence); // 아이디값 세팅(시스템이 정해주는 것)
        store.put(member.getId(), member); // 스토어 Map에 저장
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id)); // 옵셔널로 감사서 반환
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name)) // 네임이 같은 경우에만 필터링됨
                .findAny(); // 찾으면 그걸 반환함. 없으면 Optional에 null이 반환됨.
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
