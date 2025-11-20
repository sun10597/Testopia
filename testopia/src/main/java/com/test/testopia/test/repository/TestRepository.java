package com.test.testopia.test.repository;

import com.test.testopia.test.entity.TestEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestRepository extends JpaRepository<TestEntity, Long> {

    // 💡 1. TestInitializer에서 사용: 테스트 이름으로 엔티티를 찾기 위한 메서드
    Optional<TestEntity> findByName(String name);

    // 2. 기존 메서드: 테스트 + 질문 + 보기까지 한 번에 가져오기 위한 fetch join 대용
    @EntityGraph(attributePaths = {"questions"})
    Optional<TestEntity> findWithQuestionsById(Long id);
}