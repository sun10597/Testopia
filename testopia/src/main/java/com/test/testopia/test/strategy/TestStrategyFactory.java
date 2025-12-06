package com.test.testopia.test.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 테스트 이름에 따라 적절한 결과 계산 전략(Strategy)을 찾아주는 팩토리 클래스입니다.
 */
@Component
public class TestStrategyFactory {

    private final Map<String, ITestResultStrategy> strategies;

    @Autowired
    public TestStrategyFactory(List<ITestResultStrategy> strategyList) {
        // 스프링이 주입한 모든 ITestResultStrategy 구현체를 Map에 저장합니다.
        // 키(Key)는 각 전략이 반환하는 테스트 이름입니다.
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        ITestResultStrategy::getTestName,
                        Function.identity()
                ));
    }

    /**
     * 테스트 이름에 해당하는 전략 구현체를 반환합니다.
     */
    public ITestResultStrategy getStrategy(String testName) {
        ITestResultStrategy strategy = strategies.get(testName);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 테스트 유형입니다: " + testName);
        }
        return strategy;
    }
}