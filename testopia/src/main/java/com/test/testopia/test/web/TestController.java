package com.test.testopia.test.web;

import com.test.testopia.auth.DTO.MemberVO;
import com.test.testopia.test.entity.TestEntity;
import com.test.testopia.test.DTO.TestResultVO;
import com.test.testopia.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;

    // 테스트 페이지 조회
    @GetMapping("/{testId}")
    public String showTest(@PathVariable Long testId, Model model,
                           @AuthenticationPrincipal OAuth2User oAuth2User) {
        TestEntity test = testService.getTestWithQuestions(testId);
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

        model.addAttribute("test", test);
        model.addAttribute("questions", test.getQuestions());

        return "testPage";
    }

    @PostMapping("/{testId}/submit")
    public String submitTest(
            @PathVariable Long testId,
            @RequestParam Map<String, String> paramMap,
            @AuthenticationPrincipal OAuth2User oAuth2User,
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

        // 1) 테스트 정보
        TestEntity test = testService.getTestWithQuestions(testId);

        // 2) 점수 계산 + 결과 유형 (메서드 이름 변경)
        TestResultVO result = testService.calculateTestResult(testId, paramMap); // ★ 수정!

        model.addAttribute("test", test);
        model.addAttribute("totalScore", result.getTotalScore());
        model.addAttribute("resultType", result.getResultType());
        model.addAttribute("resultDescription", result.getDescription());

        return "resultPage";
    }
}
