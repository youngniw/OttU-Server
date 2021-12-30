package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Comment")
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @Column(name = "comment_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_idx;

    @Column(name = "comment_writer")
    private String comment_writer;

    @Column(name = "comment_date")
    private Date comment_date;

    @Column(name = "comment_context",nullable = false)
    private String comment;




}
