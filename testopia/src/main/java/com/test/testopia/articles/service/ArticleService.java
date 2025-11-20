package com.test.testopia.articles.service;

import com.test.testopia.articles.entity.ArticleEntity;
import com.test.testopia.articles.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    // 💡 1. 전체 목록 조회: Entity List를 VO List로 변환
    // ArticleRepository.findAllWithMember() 사용 가정
    public List<ArticleVO> selectArticleList(){
        // List<ArticleEntity> entities = (List<ArticleEntity>) articleRepository.findAll(); // 기존 코드
        List<ArticleEntity> entities = articleRepository.findAllWithMember(); // 💡 JOIN FETCH 사용
        return entities.stream().map(ArticleVO::new).collect(Collectors.toList());
    }

    // 💡 2. 게시글 작성: ArticleEntity 대신 ArticleVO와 memberId를 받도록 시그니처 변경
    public ArticleEntity createArticle(ArticleVO vo, Long memId) {
        // VO에서 Entity로 변환하며 작성자 ID를 설정하여 새 ArticleEntity 생성
        ArticleEntity entity = new ArticleEntity(vo.getTitle(), vo.getContent(), memId);

        return articleRepository.save(entity);
    }

    // 💡 3. 상세 조회: Entity 반환은 유지하되, 내부에서 JOIN FETCH 사용
    // ArticleRepository.findByIdWithMember(id) 사용 가정
    public ArticleEntity viewArticle(Long id) {
        return articleRepository.findByIdWithMember(id).orElse(null);
    }

    // 4. 삭제 (변경 없음)
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    // 💡 5. 업데이트: ArticleVO를 인자로 받고 작성자 ID가 유지되도록 처리
    public ArticleVO updateArticle(ArticleVO vo) {
        // 1. 기존 엔티티 조회 (memberId 유지를 위해)
        ArticleEntity existingEntity = articleRepository.findById(vo.getId()).orElse(null);
        if (existingEntity == null) return null;

        // 2. 내용 업데이트하고 memberId는 기존 값을 유지
        existingEntity.setTitle(vo.getTitle());
        existingEntity.setContent(vo.getContent());

        ArticleEntity update = articleRepository.save(existingEntity);
        // 저장 후 ArticleVO로 변환하여 반환
        return new ArticleVO(update);
    }
}