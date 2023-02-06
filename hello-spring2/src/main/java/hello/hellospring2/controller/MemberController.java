package hello.hellospring2.controller;

import hello.hellospring2.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {
//    @Autowired private final MemberService memberService; // 필드 주입 방법 (별로 안 좋음)

    private MemberService memberService;

    @Autowired // 생성자 주입 (가장 좋은 방법)
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

//    @Autowired // Setter 주입 -> 누군가가 호출할 때 public으로 노출되어야 함. 중간에 잘못 바꾸면 문제가 될 수 있음.
//    public void setMemberService(MemberService memberService) {
//        this.memberService = memberService;
//    }
}


