package com.test.testopia.admin.web;

import com.test.testopia.admin.service.AdminService;
import com.test.testopia.auth.DTO.MemberForm;
import com.test.testopia.auth.DTO.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class AdminController {
    @Autowired
    AdminService  adminService;

    private MemberVO getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            Object userAttribute = oAuth2User.getAttributes().get("member");
            if (userAttribute instanceof MemberVO memberVO) {
                return memberVO;
            }
        }
        return null;
    }

    private boolean isAdmin(MemberVO user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return "1".equals(user.getRole());
    }

    @GetMapping("/list")
    public String articleList(
            Model model,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            RedirectAttributes redirectAttributes) {

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

        MemberVO currentUser = getCurrentUser(oAuth2User);
        if (currentUser == null || !isAdmin(currentUser)) {
            redirectAttributes.addFlashAttribute("msg", "관리자 권한이 없습니다.");
            return "redirect:/";
        }

        List<MemberVO> memberList= adminService.selectMemberList();
        model.addAttribute("memberList", memberList);
        return "admin/list";
    }

    @GetMapping("/view/{memId}")
    public String viewMember(
            @PathVariable("memId") Long memId,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            Model model,
            RedirectAttributes redirectAttributes) {
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

        if (getCurrentUser(oAuth2User) == null || !isAdmin(getCurrentUser(oAuth2User))) {
            redirectAttributes.addFlashAttribute("msg", "관리자 권한이 없습니다.");
            return "redirect:/";
        }

        MemberVO memberVO = adminService.findById(memId);
        if (memberVO == null) {
            redirectAttributes.addFlashAttribute("msg", "해당 회원을 찾을 수 없습니다.");
            return "redirect:/admin/members/list";
        }
        model.addAttribute("memberVO", memberVO);
        return "admin/view";
    }

    @GetMapping("/edit/{memId}")
    public String editMember(
            @PathVariable("memId") Long memId,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            Model model,
            RedirectAttributes redirectAttributes) {
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

        if (getCurrentUser(oAuth2User) == null || !isAdmin(getCurrentUser(oAuth2User))) {
            redirectAttributes.addFlashAttribute("msg", "관리자 권한이 없습니다.");
            return "redirect:/";
        }
        MemberVO memberVO = adminService.findById(memId);
        if (memberVO == null) {
            redirectAttributes.addFlashAttribute("msg", "해당 회원을 찾을 수 없습니다.");
            return "redirect:/admin/members/list";
        }

        model.addAttribute("memberVO", memberVO);
        return "admin/edit";
    }

    @PostMapping("/editProc")
    public String editMemberProc(
            MemberForm form,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            RedirectAttributes redirectAttributes) {

        if (getCurrentUser(oAuth2User) == null || !isAdmin(getCurrentUser(oAuth2User))) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/";
        }

        MemberVO updatedVO = adminService.updateMember(form);

        if (updatedVO == null) {
            redirectAttributes.addFlashAttribute("msg", "회원 수정에 실패했습니다.");
            return "redirect:/admin/members/edit/" + form.getMemId();
        } else {
            redirectAttributes.addFlashAttribute("msg", "회원 정보가 성공적으로 수정되었습니다.");
        }

        return "redirect:/admin/members/list";
    }

    @GetMapping("/delete/{memId}")
    public String deleteMember(
            @PathVariable("memId") Long memId,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            RedirectAttributes redirectAttributes) {

        if (getCurrentUser(oAuth2User) == null || !isAdmin(getCurrentUser(oAuth2User))) {
            redirectAttributes.addFlashAttribute("msg", "관리자 권한이 없습니다.");
            return "redirect:/";
        }

        try {
            adminService.deleteMember(memId);
            redirectAttributes.addFlashAttribute("msg", "회원이 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "회원 삭제에 실패했습니다.");
        }

        return "redirect:/admin/members/list";
    }

}
