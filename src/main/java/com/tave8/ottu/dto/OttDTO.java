package com.tave8.ottu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OttDTO {
    private int platformIdx;
    private Long userIdx;
    private int headcount;
    private int paymentDay;
}
