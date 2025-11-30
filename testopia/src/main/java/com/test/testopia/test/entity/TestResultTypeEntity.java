package com.test.testopia.test.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime; // LocalDateTime 임포트

@Entity
@Table(name = "test_result_type")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TestResultTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private TestEntity test;

    @Column(nullable = false)
    private String resultName;

    @Column(length = 2000)
    private String description;

    private int minScore;
    private int maxScore;

}