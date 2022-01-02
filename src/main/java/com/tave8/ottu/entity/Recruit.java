package com.tave8.ottu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long choiceNum = 0L;

    @Column(name = "is_completed", columnDefinition = "boolean default false")
    private Boolean isCompleted;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @JsonIgnore
    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDate;

    @JsonIgnore
    @Column(name = "is_team")
    private Boolean isTeam;

    @PrePersist
    public void prePersist() {
        this.isCompleted = this.isCompleted == null ? false : this.isCompleted;
        this.isTeam = this.isTeam == null ? false : this.isTeam;
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
