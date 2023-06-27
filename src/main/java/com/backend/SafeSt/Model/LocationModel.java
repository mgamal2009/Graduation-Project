package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationModel {
    private Integer id;
    private String longitude;
    private String latitude;
//    private String color;
    private double averageScore;
}
