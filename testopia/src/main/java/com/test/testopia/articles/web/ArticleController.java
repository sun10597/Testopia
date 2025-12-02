package com.test.testopia.articles.web;

import com.test.testopia.articles.service.ArticleForm;
import com.test.testopia.articles.service.ArticleService;
import com.test.testopia.articles.service.ArticleVO;
import com.test.testopia.auth.DTO.MemberVO;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    private MemberVO getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            Object userAttribute = oAuth2User.getAttributes().get("member");
            if (userAttribute instanceof MemberVO memberVO) {
                return memberVO;
            }
        }
        return null;
    }

    // 💡 Helper 함수: 관리자 권한 확인 (String to int 변환)
    private boolean isAdmin(MemberVO user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return "1".equals(user.getRole());
    }


    @GetMapping("/articles/new")
    public String newArticle(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            Model model){
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

        return "article/new";
    }

    @PostMapping("/articles/newProc")
    public String newArticleProc(
            ArticleForm form,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            RedirectAttributes redirectAttributes){

        MemberVO currentUser = getCurrentUser(oAuth2User);
        Long memId = currentUser != null ? currentUser.getMemId() : null;

        if (memId == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        ArticleVO voForCreation = new ArticleVO();
        voForCreation.setTitle(form.getTitle());
        voForCreation.setContent(form.getContent());

        articleService.createArticle(voForCreation, memId);

        return "redirect:/articles/list";
    }

    // 상세 조회 - 열람 권한 검증 추가
    @GetMapping("/articles/view/{id}")
    public String articleView(
            @PathVariable(value = "id") Long id,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            Model model,
            RedirectAttributes redirectAttributes){
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
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        ArticleVO articleVO = articleService.viewArticle(id);
        if (articleVO == null) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/list";
        }

        // 권한 검증: 작성자(memId 일치) 또는 관리자(Role == 1)
        boolean isAuthor = articleVO.getMemId().equals(currentUser.getMemId());
        boolean isAdmin = isAdmin(currentUser); // 💡 수정된 부분
        boolean canAccess = isAuthor || isAdmin;

        if (!canAccess) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/list";
        }

        model.addAttribute("articleVO", articleVO);
        model.addAttribute("canModify", isAuthor || isAdmin);

        return "article/view";
    }

    @GetMapping("/articles/list")
    public String articleList(
            Model model,
            @AuthenticationPrincipal OAuth2User oAuth2User){
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

        List<ArticleVO> articleList= articleService.selectArticleList();
        model.addAttribute("articleList", articleList);
        return "article/list";
    }

    // 삭제 처리 - 삭제 권한 검증 추가
    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(
            @PathVariable(value = "id") Long id,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            RedirectAttributes redirectAttributes){

        MemberVO currentUser = getCurrentUser(oAuth2User);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        ArticleVO articleVO = articleService.viewArticle(id);
        if (articleVO == null) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/list";
        }

        // 권한 검증 로직
        boolean isAuthor = articleVO.getMemId().equals(currentUser.getMemId());
        boolean isAdmin = isAdmin(currentUser); // 💡 수정된 부분

        if (!isAuthor && !isAdmin) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/view/" + id;
        }

        articleService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("msg","삭제되었습니다.");
        return "redirect:/articles/list";
    }

    // 수정 페이지 - 수정 권한 검증 추가
    @GetMapping("/articles/edit/{id}")
    public String articleEdit(
            @PathVariable(value = "id") Long id,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            Model model,
            RedirectAttributes redirectAttributes){
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
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        ArticleVO articleVO = articleService.viewArticle(id);
        if (articleVO == null) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/list";
        }

        // 권한 검증 로직
        boolean isAuthor = articleVO.getMemId().equals(currentUser.getMemId());
        boolean isAdmin = isAdmin(currentUser); // 💡 수정된 부분

        if (!isAuthor && !isAdmin) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/view/" + id;
        }

        model.addAttribute("articleVO", articleVO);
        return  "article/edit";
    }

    // 수정 처리 - 권한 검증 추가
    @PostMapping("/articles/editProc")
    public String articleEditProc(
            RedirectAttributes redirectAttributes,
            ArticleForm form,
            @AuthenticationPrincipal OAuth2User oAuth2User){

        MemberVO currentUser = getCurrentUser(oAuth2User);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        ArticleVO existingVO = articleService.viewArticle(form.getId());
        if (existingVO == null) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/list";
        }

        // 권한 검증 로직 (2차 방어)
        boolean isAuthor = existingVO.getMemId().equals(currentUser.getMemId());
        boolean isAdmin = isAdmin(currentUser); // 💡 수정된 부분

        if (!isAuthor && !isAdmin) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/view/" + form.getId();
        }


        ArticleVO voForUpdate = new ArticleVO();
        voForUpdate.setId(form.getId());
        voForUpdate.setTitle(form.getTitle());
        voForUpdate.setContent(form.getContent());

        ArticleVO updateVO = articleService.updateArticle(voForUpdate);

        if (updateVO == null) {
            redirectAttributes.addFlashAttribute("msg", "권한이 없습니다.");
            return "redirect:/articles/list";
        }

        redirectAttributes.addFlashAttribute("msg","수정되었습니다.");
        return "redirect:/articles/view/" + updateVO.getId();
    }
}