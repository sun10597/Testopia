package com.test.testopia.articles.entity;

import com.test.testopia.auth.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "articles")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String content;

    @Column(name = "mem_id")
    private Long memId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id", insertable = false, updatable = false)
    private MemberEntity member;

    public ArticleEntity(String title, String content, Long memId) {
        this.title = title;
        this.content = content;
        this.memId = memId;
    }

    public ArticleEntity(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
