package com.test.testopia.test.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Integer version;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC")
    @Builder.Default
    private List<QuestionEntity> questions = new ArrayList<>();

    // ===== [추가된 부분 1] TestResultTypeEntity 컬렉션 =====
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestResultTypeEntity> resultTypes = new ArrayList<>();
    // =======================================================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (version == null) {
            version = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 연관관계 편의 메서드: Question 추가
    public void addQuestion(QuestionEntity question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
        question.setTest(this);
    }

    // ===== [추가된 부분 2] 연관관계 편의 메서드: ResultType 추가 =====
    public void addResultType(TestResultTypeEntity resultType) {
        if (resultTypes == null) {
            resultTypes = new ArrayList<>();
        }
        resultTypes.add(resultType);
        // TestResultTypeEntity에 setTest(this) 메서드가 있어야 함
        resultType.setTest(this);
    }
    // ===============================================================
}