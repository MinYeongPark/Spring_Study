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

            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            em.persist(member);

            team.addMember(member);

            em.flush(); // 현재 영속성 컨텍스트에 있는 DB에 쿼리를 다 날려서
            em.clear(); // 영속성 컨텍스트 초기화

            Team findTeam = em.find(Team.class, team.getId()); // 1차 캐시에만 들어가 있음
            List<Member> members = findTeam.getMembers(); // 여기에 값이 없음.
            System.out.println("===================");
            System.out.println("members = " + findTeam);
            System.out.println("===================");

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
