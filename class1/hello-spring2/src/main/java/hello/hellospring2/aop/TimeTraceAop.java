package hello.hellospring2.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component // 스프링 빈에 등록해야 하는데 이렇게 Component 애노테이션 찍어줘도 되지만 스프링 빈에 등록해서 쓰는 것을 더 선호함. 일단 이 예제에서는 컴포넌트 스캔을 쓰겠다고 하심.
public class TimeTraceAop {
    @Around("execution(* hello.hellospring2..*(..))") // 이 경로 패키지 하위에 다 적용하겠다
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("START : " + joinPoint.toString());

        try {
            return joinPoint.proceed(); // joinPoint.proceed() : 다음 메서드로 진행됨.
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END : " + joinPoint.toString() + " " + timeMs + "ms");
        }
    }
}
