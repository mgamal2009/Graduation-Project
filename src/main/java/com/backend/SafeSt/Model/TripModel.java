package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripModel {
    private Integer id;
    private String startedAt;
    private String estimatedEnd;
    private int estimatedTime;
    private long remainingTime;
    private boolean ended;
    private double sourceLongitude;
    private double sourceLatitude;
    private double destinationLongitude;
    private double destinationLatitude;
    private Integer customerId;
}
