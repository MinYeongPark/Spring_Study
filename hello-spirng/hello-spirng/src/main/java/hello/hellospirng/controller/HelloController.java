package hello.hellospirng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello") // 웹어플리케이션에서 /hello로 들어오면 이 메서드를 호출함
    public String hello(Model model) {
        model.addAttribute("data", "hello!!");
        return "hello";
    }
}

