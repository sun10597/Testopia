package com.test.testopia.articles.dto; // Controller와 가까운 패키지에 위치

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 💡 Spring Data Binding을 위해 필수
public class ArticleForm {
    private Long id;
    private String title;
    private String content;
}
