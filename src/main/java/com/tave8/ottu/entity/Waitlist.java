package com.tave8.ottu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "waitlist")
@NoArgsConstructor
@Getter
@Setter
public class Waitlist {
    @Id
    @Column(name = "waitlist_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long waitlistIdx;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recruit_idx")
    private Recruit recruit;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "is_accepted", columnDefinition = "boolean default false")
    private Boolean isAccepted;

    @PrePersist
    public void prePersist() {
        this.isAccepted = this.isAccepted == null ? false : this.isAccepted;
    }

    @Override
    public String toString() {
        return "Waitlist{" +
                "waitlistIdx=" + waitlistIdx +
                ", recruit=" + recruit +
                ", user=" + user +
                ", isAccepted=" + isAccepted +
                '}';
    }
}
