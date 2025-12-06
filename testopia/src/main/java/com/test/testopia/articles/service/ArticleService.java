package com.test.testopia.articles.service;

import com.test.testopia.articles.dto.ArticleVO;
import com.test.testopia.articles.entity.ArticleEntity;
import com.test.testopia.articles.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<ArticleVO> selectArticleList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        return articleRepository.findAllWithMember(pageable)
                .map(ArticleVO::from);
    }

    public ArticleVO createArticle(ArticleVO vo, Long memId) {
        ArticleEntity entity = new ArticleEntity(vo.getTitle(), vo.getContent(), memId);
        ArticleEntity savedEntity = articleRepository.save(entity);

        // ✅ 저장된 엔티티를 VO로 변환
        return ArticleVO.from(savedEntity);
    }

    public ArticleVO viewArticle(Long id) {
        Optional<ArticleEntity> entity = articleRepository.findByIdWithMember(id);

        if (entity.isEmpty()) {
            return null;
        }

        // ✅ 여기서도 from 사용
        return ArticleVO.from(entity.get());
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public ArticleVO updateArticle(ArticleVO vo) {
        ArticleEntity existingEntity = articleRepository.findById(vo.getId()).orElse(null);
        if (existingEntity == null) return null;

        existingEntity.setTitle(vo.getTitle());
        existingEntity.setContent(vo.getContent());

        ArticleEntity updatedEntity = articleRepository.save(existingEntity);
        return ArticleVO.from(updatedEntity);
    }
}
