package kr.java.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 1-10
@Controller
public class PageController {

    @GetMapping
    public String index() {
        return "index";
    }
}
