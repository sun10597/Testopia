package com.test.testopia.articles.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.test.testopia.auth.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;

    @Column(name = "mem_id", nullable = false)
    private Long memId;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;
    @CreationTimestamp
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id", insertable = false, updatable = false)
    private MemberEntity member;

    public ArticleEntity(String title, String content, Long memId) {
        this.title = title;
        this.content = content;
        this.memId = memId;
    }
}
