package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

//    @Query(name = "Member.findByUsername") // Member 엔티티에 달아둔 @NamedQuery를 찾아서 실행해준다. // 이 어노테이션 없어도 잘 찾아와짐!
    List<Member> findByUsername(@Param("username") String username); // :username 이런 식으로 파라미터가 있을 때 @Param 사용
}
