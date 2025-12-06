package com.test.testopia.test.strategy.testSrategy;

import com.test.testopia.test.entity.ChoiceEntity;
import com.test.testopia.test.entity.TestEntity;
import com.test.testopia.test.entity.TestResultTypeEntity;
import com.test.testopia.test.repository.ChoiceRepository;
import com.test.testopia.test.repository.TestResultTypeRepository;
import com.test.testopia.test.dto.TestResultVO;
import com.test.testopia.test.strategy.ITestResultStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MbtiStrategy implements ITestResultStrategy {

    private static final int QUESTIONS_PER_AXIS = 10;
    private static final int AXIS_MID_SCORE = (QUESTIONS_PER_AXIS * 3) / 2; // 15점

    @Override
    public String getTestName() {
        return "MBTI 40문항 성격 유형 테스트"; // Initializer와 이름 일치 필수
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

        // 2. 선택지 엔티티 조회 (Question 정보 포함)
        // ChoiceRepository.findAllWithQuestionById(List<Long> ids) 사용
        List<ChoiceEntity> choices = choiceRepository.findAllWithQuestionById(choiceIds);

        // 3. 4가지 축별 점수 계산
        Map<Integer, Integer> axisScores = choices.stream()
                .collect(Collectors.groupingBy(
                        // 질문 번호(orderNo)를 10으로 나누어 축 번호(0:E/I, 1:S/N, 2:T/F, 3:J/P) 분류
                        choice -> (choice.getQuestion().getOrderNo() - 1) / QUESTIONS_PER_AXIS,
                        Collectors.summingInt(ChoiceEntity::getScore)
                ));

        // 4. MBTI 유형 문자열 생성 (예: 'ENTJ')
        String mbtiType = calculateMbtiType(axisScores);

        // 5. DB에서 최종 결과 유형 조회 (점수 대신 이름으로 조회)
        TestResultTypeEntity resultType = resultTypeRepository.findByTestIdAndResultName(testId, mbtiType)
                .orElseThrow(() -> new IllegalStateException("MBTI 결과 유형(" + mbtiType + ") 정의가 없습니다."));

        // 전체 총점 (부가 정보)
        int totalScore = axisScores.values().stream().mapToInt(Integer::intValue).sum();

        return new TestResultVO(totalScore, resultType.getResultName(), resultType.getDescription());
    }

    private String calculateMbtiType(Map<Integer, Integer> axisScores) {
        StringBuilder mbti = new StringBuilder();
        char[] primaryTypes = {'E', 'S', 'T', 'J'};
        char[] secondaryTypes = {'I', 'N', 'F', 'P'};

        for (int i = 0; i < 4; i++) {
            Integer score = axisScores.getOrDefault(i, 0);

            // 15점 초과: E, S, T, J
            if (score > AXIS_MID_SCORE) {
                mbti.append(primaryTypes[i]);
            }
            // 15점 이하: I, N, F, P
            else {
                mbti.append(secondaryTypes[i]);
            }
        }
        return mbti.toString();
    }
}