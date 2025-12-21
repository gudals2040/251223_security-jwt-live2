package kr.java.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    // 메인 테스트 페이지
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
