package com.test.testopia.test.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class TestResultVO {
    private int totalScore;
    private String resultType;
    private String description;
}
