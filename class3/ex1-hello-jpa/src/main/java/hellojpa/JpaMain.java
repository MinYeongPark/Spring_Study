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
            em.persist(member); //

            // flush는 -> commit할 때나 query가 날아갈 때 동작함

            em.flush();

            // dbconn.executeQuery("select * from member"); // 이때는 flush 안 됨
            // DB에는 값이 없는 상태!
            // 결과가 0으로 나옴

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
