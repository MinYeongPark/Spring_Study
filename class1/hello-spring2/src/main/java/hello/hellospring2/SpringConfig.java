package hello.hellospring2;

import hello.hellospring2.aop.TimeTraceAop;
import hello.hellospring2.repository.*;
import hello.hellospring2.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final MemberRepository memberRepository;

    @Autowired // 생성자가 1개 이면 생략 가능
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //    private EntityManager em;
//
//    @Autowired
//    public SpringConfig(EntityManager em) {
//        this.em = em;
//    }

    //    private DataSource dataSource; // 스프링이 데이터베이스와 연결할 수 있는 정보가 있는 것을 만들어주고 주입해줌.
//
//    @Autowired
//    public SpringConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }


    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

//    @Bean
//    public MemberService memberService() {
//        return new MemberService(memberRepository());
//    }

//    @Bean
//    public MemberRepository memberRepository() {
//        return new MemoryMemberRepository();
//        return new JdbcMemberRepository(dataSource); // 이 조립하는 부분만 수정하면 딱 연결되는 게 스프링의 장점.
//        return new JdbcTemplateMemberRepository(dataSource);
//        return new JpaMemberRepository(em);
//    }

//    @Bean // AOP는 정형화된 것은 아니어서 스프링빈에 직접 등록해주면 좋음.
//    public TimeTraceAop TimeTraceAop() {
//        return new TimeTraceAop();
//    }
}