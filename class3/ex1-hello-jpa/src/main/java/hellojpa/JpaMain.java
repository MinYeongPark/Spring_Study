package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin(); // DB 트랜잭션 시작

        try {

            Member member = new Member();
            member.setUsername("member1");

            em.persist(member);

            Team team = new Team();
            team.setName("teamA");
            team.getMembers().add(member);

            em.persist(team);

            tx.commit(); // 트랜잭션 커밋 시 아무런 일도 일어나지 않게 됨.
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
