package com.backend.SafeSt.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationReq {
//    private Integer id;

    private double longitude;
    private double latitude;
//    private String color;
//    private float averageScore;
}
