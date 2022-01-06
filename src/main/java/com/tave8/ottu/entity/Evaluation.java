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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationIdx;

    @OneToOne
    @JoinColumn(name="user_idx")
    private User user;

    @Column(name = "count")
    private Integer count;

    @Column(name = "reliability")
    private Double reliability;

    @PrePersist
    public void prePersist() {
        this.count = this.count == null ? 2 : this.count;
        this.reliability = this.reliability == null ? 10 : this.reliability;
    }
}
