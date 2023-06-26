package com.backend.SafeSt.Request;

import com.backend.SafeSt.Util.TimestampDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripReq {
    private Integer id;

    @JsonDeserialize(using = TimestampDeserializer.class )
    private Timestamp estimatedTime;
    private Timestamp totalTime;
    private int addMin;
    private boolean ended;
    private double sourceLongitude;
    private double sourceLatitude;
    private double destinationLongitude;
    private double destinationLatitude;

    private Integer customerId;
}
