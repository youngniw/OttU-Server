package com.tave8.ottu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "team")
@NoArgsConstructor
@Getter
@Setter
public class Team {
    @Id
    @Column(name = "team_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamIdx;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "leader_idx")
    private User leader;                        //팀장 정보(다대일 연관관계)

    @ManyToOne
    @JoinColumn(name = "platform_idx")
    private Platform platform;                  //플랫폼 정보(다대일 연관관계)

    @Column
    private int headcount;

    @JsonIgnore
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "payment_day")
    private int paymentDay;

    @Transient
    private LocalDate paymentDate;

    @JsonIgnore
    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        this.isDeleted = this.isDeleted == null ? false : this.isDeleted;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamIdx=" + teamIdx +
                ", leader=" + leader +
                ", platform=" + platform +
                ", headcount=" + headcount +
                ", createdDate=" + createdDate +
                ", paymentDay=" + paymentDay +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
