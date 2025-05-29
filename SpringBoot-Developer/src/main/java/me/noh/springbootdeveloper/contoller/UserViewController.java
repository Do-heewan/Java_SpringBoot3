package me.noh.springbootdeveloper.contoller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserViewController {
//    @GetMapping("/login")
//    public String login() {
//        return "oauthlogin";
//    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // 카카오 로그인 추가
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/login")
    public String login(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
        model.addAttribute("location", location);

        return "oauthlogin";
    }
}