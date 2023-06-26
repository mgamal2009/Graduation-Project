package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripModel {
    private Integer id;
    private String estimatedTime;
    private String totalTime;
    private boolean ended;
    private double sourceLongitude;
    private double sourceLatitude;
    private double destinationLongitude;
    private double destinationLatitude;
    private Integer customerId;
}
