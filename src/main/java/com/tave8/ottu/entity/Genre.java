package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "genre")
@NoArgsConstructor
@Getter
@Setter
public class Genre {
    @Id
    @Column(name = "genre_idx")
    private Long genreIdx;

    @Column(name = "genre_name")
    private String genreName;
}
