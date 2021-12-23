package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToOne
    @JoinColumn(name = "platform_idx")
    private Platform platform;                  //플랫폼 정보(일대일 연관관계)

    @ManyToOne
    @JoinColumn(name="user_idx")
    private User writer;                        //작성자 정보(다대일 연관관계)

    @Column
    private int headcount;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
