package com.test.testopia.test.DTO;

import lombok.*;

@Getter
@AllArgsConstructor
public class TestResultVO {
    private int totalScore;
    private String resultType;
    private String description;
}
