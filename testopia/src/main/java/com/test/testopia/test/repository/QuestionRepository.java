package com.test.testopia.test.repository;
import com.test.testopia.test.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByTestIdOrderByOrderNoAsc(Long testId);
}
