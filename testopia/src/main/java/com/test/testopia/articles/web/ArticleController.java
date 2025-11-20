package com.test.testopia.articles.web;
import com.test.testopia.articles.entity.ArticleEntity;
import com.test.testopia.articles.service.ArticleForm;
import com.test.testopia.articles.service.ArticleService;
import com.test.testopia.articles.service.ArticleVO;
import com.test.testopia.auth.service.MemberVO; // 💡 MemberVO import
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 💡 import
import org.springframework.security.oauth2.core.user.OAuth2User; // 💡 import
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
        System.err.println("새글 쓰기");
        return "article/new";
    }

    // 💡 새 글 작성 처리 (작성자 정보 추가)
    @PostMapping("/articles/newProc")
    public String newArticleProc(
            ArticleForm form,
            @AuthenticationPrincipal OAuth2User oAuth2User){ // 💡 사용자 정보 받기

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

        // 💡 ArticleForm 데이터를 사용하여 ArticleVO 생성
        ArticleVO voForCreation = new ArticleVO(); // NoArgsConstructor 사용
        voForCreation.setTitle(form.getTitle());
        voForCreation.setContent(form.getContent());

        // 💡 Service 호출
        ArticleEntity result = articleService.createArticle(voForCreation, memId);

        return "redirect:/articles/list";
    }

    // 💡 상세 조회 (ArticleEntity 반환 유지)
    @GetMapping("/articles/view/{id}")
    public String articleView( // 메소드 이름 수정: articleList -> articleView
                               @PathVariable(value = "id") Long id, Model model){
        // ArticleService에서 JOIN FETCH된 ArticleEntity 반환
        ArticleEntity articleEntity = articleService.viewArticle(id);
        model.addAttribute("articleEntity",articleEntity);
        // 템플릿에서 articleEntity.getMember().getMemName()으로 작성자 이름 접근
        return "article/view";
    }

    // 💡 목록 조회 (ArticleVO List 반환하도록 변경)
    @GetMapping("/articles/list")
    public String articleList(Model model){
        List<ArticleVO> articleList= articleService.selectArticleList(); // 💡 VO List를 받음
        model.addAttribute("articleList", articleList); // 💡 모델 이름을 articleList에 맞게 수정
        // 템플릿에서 ${article.memName}으로 작성자 이름 접근
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
        ArticleEntity articleEntity = articleService.viewArticle(id);
        model.addAttribute("articleEntity",articleEntity);
        return  "article/edit";
    }

    // 💡 수정 처리 (ArticleVO를 Service로 전달)
    @PostMapping("/articles/editProc")
    public String articleEditProc(
            RedirectAttributes redirectAttributes,
            ArticleForm form){

        // ArticleService에서 VO를 받도록 변경 (기존 ArticleEntity를 반환하지 않고 VO 반환)
        ArticleVO voForUpdate = new ArticleVO(); // NoArgsConstructor 사용
        voForUpdate.setId(form.getId());
        voForUpdate.setTitle(form.getTitle());
        voForUpdate.setContent(form.getContent());

        // 💡 Service 호출
        ArticleVO updateVO = articleService.updateArticle(voForUpdate);

        redirectAttributes.addFlashAttribute("msg","수정되었습니다.");
        return "redirect:/articles/view/" + updateVO.getId();
    }

}