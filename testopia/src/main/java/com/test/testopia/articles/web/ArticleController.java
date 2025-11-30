package com.test.testopia.articles.web;

import com.test.testopia.articles.service.ArticleForm;
import com.test.testopia.articles.service.ArticleService;
import com.test.testopia.articles.service.ArticleVO;
import com.test.testopia.auth.service.MemberVO;
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

    @GetMapping("/articles/new")
    public String newArticle(){
        return "article/new";
    }

    @PostMapping("/articles/newProc")
    public String newArticleProc(
            ArticleForm form,
            @AuthenticationPrincipal OAuth2User oAuth2User){

        Long memId = null;
        if (oAuth2User != null) {
            Object userAttribute = oAuth2User.getAttributes().get("member");
            if (userAttribute instanceof MemberVO memberVO) {
                memId = memberVO.getMemId();
            }
        }

        if (memId == null) {
            System.err.println("🚨 로그인 상태가 아닙니다. 작성 불가.");
            return "redirect:/login";
        }

        ArticleVO voForCreation = new ArticleVO();
        voForCreation.setTitle(form.getTitle());
        voForCreation.setContent(form.getContent());

        ArticleVO result = articleService.createArticle(voForCreation, memId);

        return "redirect:/articles/list";
    }

    @GetMapping("/articles/view/{id}")
    public String articleView(
            @PathVariable(value = "id") Long id,
            Model model){

        ArticleVO articleVO = articleService.viewArticle(id);
        if (articleVO == null) {
            // 게시글이 없는 경우 처리
            return "redirect:/articles/list";
        }
        model.addAttribute("articleVO", articleVO);
        return "article/view";
    }

    @GetMapping("/articles/list")
    public String articleList(Model model){
        List<ArticleVO> articleList= articleService.selectArticleList();
        model.addAttribute("articleList", articleList);
        return "article/list";
    }

    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(
            @PathVariable(value = "id") Long id,
            RedirectAttributes redirectAttributes){
        articleService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("msg","삭제되었습니다.");
        return "redirect:/articles/list";
    }

    @GetMapping("/articles/edit/{id}")
    public String articleEdit(
            @PathVariable(value = "id") Long id,
            Model model){
        ArticleVO articleVO = articleService.viewArticle(id);
        if (articleVO == null) {
            return "redirect:/articles/list";
        }
        model.addAttribute("articleVO", articleVO);
        return  "article/edit";
    }

    @PostMapping("/articles/editProc")
    public String articleEditProc(
            RedirectAttributes redirectAttributes,
            ArticleForm form){

        ArticleVO voForUpdate = new ArticleVO();
        voForUpdate.setId(form.getId());
        voForUpdate.setTitle(form.getTitle());
        voForUpdate.setContent(form.getContent());

        ArticleVO updateVO = articleService.updateArticle(voForUpdate);

        if (updateVO == null) {
            // 업데이트 실패 (게시글 ID 없음 등) 처리
            redirectAttributes.addFlashAttribute("msg", "수정 실패: 게시글을 찾을 수 없습니다.");
            return "redirect:/articles/list";
        }

        redirectAttributes.addFlashAttribute("msg","수정되었습니다.");
        return "redirect:/articles/view/" + updateVO.getId();
    }
}