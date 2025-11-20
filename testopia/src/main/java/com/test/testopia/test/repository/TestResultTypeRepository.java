package com.test.testopia.test.repository;

import com.test.testopia.test.entity.TestResultTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestResultTypeRepository extends JpaRepository<TestResultTypeEntity, Long> {

    List<TestResultTypeEntity> findByTestId(Long testId);

    Optional<TestResultTypeEntity> findByTestIdAndMinScoreLessThanEqualAndMaxScoreGreaterThanEqual(
            Long testId, int min, int max
    );

    // 💡 MBTI 전략을 위해 추가: 테스트 ID와 결과 이름으로 정확히 일치하는 결과 유형을 찾습니다.
    Optional<TestResultTypeEntity> findByTestIdAndResultName(Long testId, String resultName);
}