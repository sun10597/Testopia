package com.test.testopia.test.strategy.testSrategy;

import com.test.testopia.test.entity.ChoiceEntity;
import com.test.testopia.test.entity.TestEntity;
import com.test.testopia.test.entity.TestResultTypeEntity;
import com.test.testopia.test.repository.ChoiceRepository;
import com.test.testopia.test.repository.TestResultTypeRepository;
import com.test.testopia.test.DTO.TestResultVO;
import com.test.testopia.test.strategy.ITestResultStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 테토/에겐 테스트와 같은 '단일 총점' 기반 테스트 결과를 계산하는 전략 구현체입니다.
 */
@Component
public class TettoStrategy implements ITestResultStrategy {

    @Override
    public String getTestName() {
        return "테토/에겐 성향 테스트"; // TestDataInitializer에 정의된 이름과 일치해야 함
    }

    @Override
    public TestResultVO calculateResult(
            Long testId,
            TestEntity test,
            Map<String, String> paramMap,
            ChoiceRepository choiceRepository,
            TestResultTypeRepository resultTypeRepository
    ) {
        // 1. 응답 ID 추출
        List<Long> choiceIds = paramMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith("q"))
                .map(e -> Long.parseLong(e.getValue()))
                .collect(Collectors.toList());

        // 2. 선택지 조회
        List<ChoiceEntity> choices = choiceRepository.findAllById(choiceIds);

        // 3. 총점 계산
        int totalScore = choices.stream().mapToInt(ChoiceEntity::getScore).sum();

        // 4. 총점을 기준으로 결과 유형 찾기 (minScore <= totalScore <= maxScore)
        TestResultTypeEntity resultType =
                resultTypeRepository.findByTestIdAndMinScoreLessThanEqualAndMaxScoreGreaterThanEqual(
                                testId, totalScore, totalScore
                        )
                        .orElseThrow(() -> new IllegalStateException("결과 유형 정의가 없습니다. (총점: " + totalScore + ")"));

        return new TestResultVO(totalScore, resultType.getResultName(), resultType.getDescription());
    }
}