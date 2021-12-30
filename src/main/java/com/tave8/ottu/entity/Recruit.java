package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruit")
@NoArgsConstructor
@Getter
@Setter
public class Recruit {
    @Id
    @Column(name = "recruit_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recruitIdx;

    @ManyToOne
    @JoinColumn(name = "platform_idx")
    private Platform platform;                  //플랫폼 정보(다대일 연관관계)

    @ManyToOne
    @JoinColumn(name="user_idx")
    private User writer;                        //작성자 정보(다대일 연관관계)

    @Column
    private int headcount;

    @Transient
    private Long choiceNum = 0L;      //DB와 상관없는 값

    @Column(name = "is_completed", columnDefinition = "boolean default false")
    private Boolean isCompleted;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;


    @PrePersist
    public void prePersist() {
        this.isCompleted = this.isCompleted == null ? false : this.isCompleted;
    }

    @Override
    public String toString() {
        return "Recruit{" +
                "recruitIdx=" + recruitIdx +
                ", platform=" + platform +
                ", writer=" + writer +
                ", headcount=" + headcount +
                ", isCompleted=" + isCompleted +
                ", createdDate=" + createdDate +
                ", choiceNum=" + choiceNum +
                '}';
    }
}
