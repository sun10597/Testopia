package com.test.testopia.test.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private TestEntity test;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(nullable = false, length = 500)
    private String text;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC")
    @Builder.Default   // ★ 이거 추가!
    private List<ChoiceEntity> choices = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addChoice(ChoiceEntity choice) {
        if (choices == null) {          // ★ 방어 코드 한 번 더
            choices = new ArrayList<>();
        }
        choices.add(choice);
        choice.setQuestion(this);
    }

    // setter는 연관관계용만 열자
    public void setTest(TestEntity test) {
        this.test = test;
    }
}
