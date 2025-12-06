package com.test.testopia.articles.dto;

import com.test.testopia.articles.entity.ArticleEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ArticleVO {

    private Long id;
    private String title;
    private String content;
    private Long memId;
    private String memName;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 🥇 정적 팩토리 메서드: 현업 1위 방식
    public static ArticleVO from(ArticleEntity entity) {
        ArticleVO vo = new ArticleVO();

        vo.id = entity.getId();
        vo.title = entity.getTitle();
        vo.content = entity.getContent();
        vo.memId = entity.getMemId();

        vo.createdAt = entity.getCreatedAt().format(FORMATTER);
        vo.updatedAt = entity.getUpdatedAt() != null
                ? entity.getUpdatedAt().format(FORMATTER)
                : "-";

        vo.memName = entity.getMember() != null
                ? entity.getMember().getMemName()
                : "알 수 없음";

        return vo;
    }
}
