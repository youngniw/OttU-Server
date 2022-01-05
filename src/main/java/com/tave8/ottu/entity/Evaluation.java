package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "evaluation")
@NoArgsConstructor
@Getter
@Setter
public class Evaluation {
    @Id
    @Column(name = "evaluation_idx")
    private Long evaluationIdx;

    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name = "count")
    private int count;

    public Evaluation(Long userIdx) {
        this.userIdx = userIdx;
    }
}
