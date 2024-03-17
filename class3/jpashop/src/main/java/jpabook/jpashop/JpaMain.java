package jpabook.jpashop;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin(); // DB 트랜잭션 시작

        try {

            Order order = em.find(Order.class, 1L);
            Member member = order.getMember();

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
