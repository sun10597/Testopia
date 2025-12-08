package com.test.testopia.test.strategy.testSrategy;

import com.test.testopia.test.dto.TestResultVO;
import com.test.testopia.test.entity.ChoiceEntity;
import com.test.testopia.test.entity.TestEntity;
import com.test.testopia.test.entity.TestResultTypeEntity;
import com.test.testopia.test.repository.ChoiceRepository;
import com.test.testopia.test.repository.TestResultTypeRepository;
import com.test.testopia.test.strategy.ITestResultStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 시간관리/우선순위 테스트
 * - 단일 총점 기반 결과 계산 전략
 */
@Component
public class TimeManagementStrategy implements ITestResultStrategy {

    private static final String TEST_NAME = "시간관리/우선순위 테스트";

    @Override
    public String getTestName() {
        return TEST_NAME;
    }

    @Override
    public TestResultVO calculateResult(
            Long testId,
            TestEntity test,
            Map<String, String> paramMap,
            ChoiceRepository choiceRepository,
            TestResultTypeRepository resultTypeRepository
    ) {
        List<Long> choiceIds = paramMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith("q"))
                .map(e -> Long.parseLong(e.getValue()))
                .collect(Collectors.toList());

        List<ChoiceEntity> choices = choiceRepository.findAllById(choiceIds);
        int totalScore = choices.stream().mapToInt(ChoiceEntity::getScore).sum();

        TestResultTypeEntity resultType =
                resultTypeRepository
                        .findByTestIdAndMinScoreLessThanEqualAndMaxScoreGreaterThanEqual(
                                testId, totalScore, totalScore
                        )
                        .orElseThrow(() ->
                                new IllegalStateException("시간관리/우선순위 테스트 결과 유형이 정의되어 있지 않습니다. (총점: " + totalScore + ")")
                        );

        return new TestResultVO(totalScore, resultType.getResultName(), resultType.getDescription());
    }
}
