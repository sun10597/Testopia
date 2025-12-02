package com.test.testopia.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.test.testopia.articles.entity.ArticleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memId;

    private String provider;     // google, kakao, naver
    private String providerId;   // 각 서비스의 고유 ID

    private String memEmail;
    private String memName;
    private String role;         // ROLE_USER, ROLE_ADMIN

    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleEntity> articles;

}
