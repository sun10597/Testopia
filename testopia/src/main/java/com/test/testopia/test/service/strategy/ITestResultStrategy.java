package com.test.testopia.test.service.strategy;

import com.test.testopia.test.service.TestResultVO;
import com.test.testopia.test.entity.TestEntity;
import com.test.testopia.test.repository.ChoiceRepository;
import com.test.testopia.test.repository.TestResultTypeRepository;

import java.util.Map;

/**
 * 모든 테스트 결과 계산 로직을 위한 전략 인터페이스.
 * 새로운 테스트 유형이 추가될 때마다 이 인터페이스를 구현합니다.
 */
public interface ITestResultStrategy {

    /**
     * 이 전략이 처리할 수 있는 테스트의 이름 (또는 식별자)을 반환합니다.
     * 예: "테토/에겐 성향 테스트", "MBTI 40문항 성격 유형 테스트"
     */
    String getTestName();

    /**
     * 사용자 응답(paramMap)을 바탕으로 최종 테스트 결과(TestResultVO)를 계산합니다.
     * @param testId 현재 진행 중인 테스트 ID
     * @param test 현재 테스트 엔티티 정보
     * @param paramMap 사용자 응답 (q1=choiceId, q2=choiceId, ...)
     * @param choiceRepository 선택지 데이터 접근을 위한 Repository
     * @param resultTypeRepository 결과 유형 데이터 접근을 위한 Repository
     * @return 계산된 최종 결과 VO
     */
    TestResultVO calculateResult(
            Long testId,
            TestEntity test,
            Map<String, String> paramMap,
            ChoiceRepository choiceRepository,
            TestResultTypeRepository resultTypeRepository
    );
}