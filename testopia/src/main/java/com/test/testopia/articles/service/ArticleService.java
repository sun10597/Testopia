package com.test.testopia.articles.service;

import com.test.testopia.articles.entity.ArticleEntity;
import com.test.testopia.articles.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<ArticleVO> selectArticleList(){
        List<ArticleEntity> entities = articleRepository.findAllWithMember();
        return entities.stream().map(ArticleVO::new).collect(Collectors.toList());
    }

    public ArticleVO createArticle(ArticleVO vo, Long memId) {
        ArticleEntity entity = new ArticleEntity(vo.getTitle(), vo.getContent(), memId);
        ArticleEntity savedEntity = articleRepository.save(entity);
        return new ArticleVO(savedEntity);
    }

    public ArticleVO viewArticle(Long id) {
        Optional<ArticleEntity> entity = articleRepository.findByIdWithMember(id);

        if (entity.isEmpty()) {
            return null;
        }

        return new ArticleVO(entity.get());
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public ArticleVO updateArticle(ArticleVO vo) {
        ArticleEntity existingEntity = articleRepository.findById(vo.getId()).orElse(null);
        if (existingEntity == null) return null;

        existingEntity.setTitle(vo.getTitle());
        existingEntity.setContent(vo.getContent());

        ArticleEntity updateEntity = articleRepository.save(existingEntity);
        return new ArticleVO(updateEntity);
    }
}