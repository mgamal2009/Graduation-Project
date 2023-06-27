package com.backend.SafeSt.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripReq {
    private Integer id;

//    @JsonDeserialize(using = TimestampDeserializer.class )
//    private Timestamp estimatedTime;
//    private Timestamp totalTime;
    private int addMin;
    private int estimatedTime;
//    private boolean ended;
    private double sourceLongitude;
    private double sourceLatitude;
    private double destinationLongitude;
    private double destinationLatitude;

    private Integer customerId;
}
