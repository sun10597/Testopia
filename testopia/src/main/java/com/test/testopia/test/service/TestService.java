package com.test.testopia.test.service;

import com.test.testopia.test.dto.TestResultVO;
import com.test.testopia.test.entity.*;
import com.test.testopia.test.repository.ChoiceRepository;
import com.test.testopia.test.repository.TestRepository;
import com.test.testopia.test.repository.TestResultTypeRepository;
import com.test.testopia.test.strategy.ITestResultStrategy;
import com.test.testopia.test.strategy.TestStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {

    private final TestRepository testRepository;
    private final ChoiceRepository choiceRepository;
    private final TestResultTypeRepository testResultTypeRepository;
    // ★ 새로 추가된 의존성
    private final TestStrategyFactory strategyFactory;

    public TestEntity getTestWithQuestions(Long testId) {
        return testRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new IllegalArgumentException("테스트를 찾을 수 없습니다. id=" + testId));
    }

    // ★ 모든 테스트의 결과 계산을 통합하는 단일 메서드
    public TestResultVO calculateTestResult(Long testId, Map<String, String> paramMap) {

        TestEntity test = getTestWithQuestions(testId);

        // 1. 테스트 이름으로 적절한 전략을 팩토리에서 가져옵니다.
        ITestResultStrategy strategy = strategyFactory.getStrategy(test.getName());

        // 2. 전략을 실행하여 결과를 계산하고 반환합니다.
        return strategy.calculateResult(
                testId,
                test,
                paramMap,
                choiceRepository,
                testResultTypeRepository
        );
    }

    // 기존의 public TestResultVO testResult(...) 메서드는 이 메서드로 대체됩니다.
}