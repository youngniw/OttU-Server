package com.tave8.ottu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "platform")
@NoArgsConstructor
@Getter
@Setter
public class Platform {
    @Id
    @Column(name = "platform_idx")
    private Long platformIdx;

    @Column(name="platform_name")
    private String platformName;
}
