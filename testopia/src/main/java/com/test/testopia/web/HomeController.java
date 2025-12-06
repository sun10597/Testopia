package com.test.testopia.web;

import com.test.testopia.auth.dto.MemberVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User oAuth2User,
                       Model model) {
        if (oAuth2User != null) {
            Object userAttribute = oAuth2User.getAttributes().get("member");
            if (userAttribute instanceof MemberVO vo) {
                model.addAttribute("name", vo.getMemName());
                boolean isAdminUser = "1".equals(vo.getRole());
                model.addAttribute("isAdmin", isAdminUser);
                System.err.println("✅ 세션에서 로드된 사용자 이름: " + vo.getMemName());
                System.err.println("✅ 세션에서 로드된 사용자 Role: " + vo.getRole());
            } else {
                model.addAttribute("name", oAuth2User.getAttribute("name"));
                model.addAttribute("isAdmin", false);
            }
        } else {
            model.addAttribute("isAdmin", false);
        }

        return "index";
    }
}