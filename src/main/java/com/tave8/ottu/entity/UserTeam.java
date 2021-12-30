package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_team")
@NoArgsConstructor
@Getter
@Setter
public class UserTeam {
    @Id
    @Column(name = "idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTeamIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_idx")
    private Team team;

    @Override
    public String toString() {
        return "UserTeam{" +
                "userTeamIdx=" + userTeamIdx +
                ", user=" + user +
                ", team=" + team +
                '}';
    }
}
