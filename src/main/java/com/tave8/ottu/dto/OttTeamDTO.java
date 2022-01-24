package com.tave8.ottu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OttTeamDTO {
    private Long recruitIdx;
    private Long userIdx;
    private int paymentDay;
}
