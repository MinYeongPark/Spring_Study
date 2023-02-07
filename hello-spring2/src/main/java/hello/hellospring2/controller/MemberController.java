package hello.hellospring2.controller;

import hello.hellospring2.domain.Member;
import hello.hellospring2.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {
    // DI 방법 3가지 : 필드 주입, setter 주입, 생성자 주입

    // 1) 필드 주입 방법 (별로 안 좋음)
//    @Autowired private final MemberService memberService;

    private MemberService memberService;

    // 2) 생성자 주입 (가장 좋은 방법)
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 3) Setter 주입 -> 누군가가 호출할 때 public으로 노출되어야 함. 중간에 잘못 바꾸면 문제가 될 수 있음.
//    @Autowired
//    public void setMemberService(MemberService memberService) {
//        this.memberService = memberService;
//    }

    @GetMapping("/members/new") // /members/new 경로로 들어오면
    public String createForm() {
        return "members/createMemberForm"; // template에서 members/createMemberForm으로 이동
    }

    @PostMapping("/members/new") // POST 방식으로 값을 받아서 이 경로로 들어오면
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/"; // 회원가입 끝나면 홈으로 보내야 함
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}


