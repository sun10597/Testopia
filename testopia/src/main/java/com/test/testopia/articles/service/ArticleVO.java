package com.test.testopia.articles.service;

import com.fasterxml.jackson.annotation.JsonFormat; // ⭐ 이 import가 필요합니다.
import com.test.testopia.articles.entity.ArticleEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
// ... (Getter, Setter 유지)

@Getter
@Setter
@NoArgsConstructor
public class ArticleVO {
    private Long id;
    private String title;
    private String content;

    private String memName;
    private Long memId;

    // 💡 created_at 필드에 포맷 적용
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 💡 updatedAt 필드에 포맷 적용 (선택 사항)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 💡 ArticleEntity를 받아서 VO를 생성하는 생성자 추가 (Service에서 사용)
    public ArticleVO(ArticleEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.memId = entity.getMemId();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();

        // 작성자 이름 로드 (Repository에서 JOIN FETCH 필수)
        if (entity.getMember() != null) {
            this.memName = entity.getMember().getMemName();
        } else {
            this.memName = "알 수 없음";
        }
    }

    // 기존 toEntity()는 수정해야 합니다. AllArgsConstructor가 변경되었기 때문입니다.
    public ArticleEntity toEntity() {
        // ID, TITLE, CONTENT만 받는 생성자가 ArticleEntity에 없으므로 수정 필요
        return new ArticleEntity(title, content, memId);
    }
}