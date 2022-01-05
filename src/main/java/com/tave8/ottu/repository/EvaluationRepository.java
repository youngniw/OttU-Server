package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation,Long> {
    Optional<Evaluation> findEvaluationByUserIdx(Long userIdx);

    @Modifying
    @Query(value = "insert into evaluation  (user_idx) value (:userIdx);",nativeQuery = true)
    void makeEvaluation (@Param("userIdx")Long userIdx);
}
