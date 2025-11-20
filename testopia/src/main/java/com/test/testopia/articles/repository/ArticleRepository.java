package com.test.testopia.articles.repository;

import com.test.testopia.articles.entity.ArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public  interface ArticleRepository extends CrudRepository<ArticleEntity, Long> {

    // 💡 목록 조회 시 작성자 정보 JOIN FETCH
    @Query("SELECT a FROM ArticleEntity a JOIN FETCH a.member")
    List<ArticleEntity> findAllWithMember();

    // 💡 상세 조회 시 작성자 정보 JOIN FETCH
    @Query("SELECT a FROM ArticleEntity a JOIN FETCH a.member WHERE a.id = :id")
    Optional<ArticleEntity> findByIdWithMember(@Param("id") Long id);
    List<ArticleEntity> findByTitleContainingIgnoreCase(String search);
}
