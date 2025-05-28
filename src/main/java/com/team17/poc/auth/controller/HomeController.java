package com.team17.poc.auth.controller;

import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /*
    @GetMapping("/")
    public String showHomepage() {
        return "index";
    }

     */

    @GetMapping("/")
    public String showHomepage(Model model) {
        model.addAttribute("ocrResult", null); // 새로 추가함.
        return "index";
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html 렌더링
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // templates/signup.html
    }

    @GetMapping("/authlogin")
    public String authloginPage() {
        return "authlogin"; // authlogin.html
    }
}

