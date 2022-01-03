package com.tave8.ottu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@NoArgsConstructor
@Getter
@Setter
public class Notice {
    @Id
    @Column(name = "notice_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeIdx;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column
    private String content;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Override
    public String toString() {
        return "Notice{" +
                "noticeIdx=" + noticeIdx +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
