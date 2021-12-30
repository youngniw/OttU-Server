package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Board")
@NoArgsConstructor
@Getter
@Setter
public class Board {
    @Id
    @Column(name = "board_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardIdx;

    @Column(name = "writer")
    private String writer;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "now")
    private Date date;

    @ManyToMany
    @JoinTable(name = "comment_context", joinColumns = @JoinColumn(name = "board_idx"), inverseJoinColumns = @JoinColumn(name = "comment_idx"))
    private List<Comment> commentList = new ArrayList<Comment>();

    public List<Comment> getComment(){
        return commentList;
    }

}
