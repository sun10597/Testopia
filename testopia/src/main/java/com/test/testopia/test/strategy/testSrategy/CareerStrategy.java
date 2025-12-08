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
 * 직업 적성 테스트
 * - 단일 총점 기반 결과 계산 전략
 */
@Component
public class CareerStrategy implements ITestResultStrategy {

    private static final String TEST_NAME = "직업 적성 테스트";

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
        // 1. 요청 파라미터에서 선택한 choiceId들 추출 (키: q1, q2, ...)
        List<Long> choiceIds = paramMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith("q"))
                .map(e -> Long.parseLong(e.getValue()))
                .collect(Collectors.toList());

        // 2. 실제 ChoiceEntity 조회
        List<ChoiceEntity> choices = choiceRepository.findAllById(choiceIds);

        // 3. 총점 산출
        int totalScore = choices.stream()
                .mapToInt(ChoiceEntity::getScore)
                .sum();

        // 4. 총점 구간에 맞는 TestResultTypeEntity 조회
        TestResultTypeEntity resultType =
                resultTypeRepository
                        .findByTestIdAndMinScoreLessThanEqualAndMaxScoreGreaterThanEqual(
                                testId, totalScore, totalScore
                        )
                        .orElseThrow(() ->
                                new IllegalStateException("직업 적성 테스트 결과 유형이 정의되어 있지 않습니다. (총점: " + totalScore + ")")
                        );

        // 5. 화면에 보여줄 VO로 변환
        return new TestResultVO(
                totalScore,
                resultType.getResultName(),
                resultType.getDescription()
        );
    }
}
