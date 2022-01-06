package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation,Long> {
    Optional<Evaluation> findByUser_UserIdx(Long userIdx);
}
