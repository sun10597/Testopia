package com.test.testopia.test.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "choice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private Integer score;

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}
