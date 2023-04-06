package hello.hellospring2.repository;

import hello.hellospring2.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository {

    private final EntityManager em; // jpa는 entityManager을 통해 동작함
    // 스프링이 현재 DB와 연결 이런 것 다 해줘서 EntityManager를 만들어줌.
    // jpa 쓰려면 EntityManager를 주입받아야 한다.

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member; // 이렇게 하면 insert 쿼리 만들어서 다 집어넣고 아이디 셋도 다 해줌.
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id); // 이렇게 find하면 조회가 됨
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
        // 객체를 대상으로 쿼리를 날림.
        // select m (객체 자체를 탐색) from Member as m
    }
}
