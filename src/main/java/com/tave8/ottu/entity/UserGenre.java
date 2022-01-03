package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_genre")
@NoArgsConstructor
@Getter
@Setter
public class UserGenre {
    @Id
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name="genre_idx")
    private int genreIdx;

    public UserGenre(Long userIdx, int genreIdx) {
        this.userIdx = userIdx;
        this.genreIdx = genreIdx;
    }
}
