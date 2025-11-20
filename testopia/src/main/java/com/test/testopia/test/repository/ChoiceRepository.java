package com.test.testopia.test.repository;

import com.test.testopia.test.entity.ChoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<ChoiceEntity, Long> {

    @Query("SELECT c FROM ChoiceEntity c JOIN FETCH c.question q WHERE c.id IN :ids")
    List<ChoiceEntity> findAllWithQuestionById(@Param("ids") List<Long> ids);
}