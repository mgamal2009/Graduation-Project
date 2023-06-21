package com.backend.SafeSt.Request;

import com.backend.SafeSt.Util.TimestampDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportReq {
    private Integer id;

    private String reportText;
    private float score;
    @JsonDeserialize(using = TimestampDeserializer.class )
    private Timestamp date;
    private double longitude;
    private double latitude;
    private Integer locationId;
    private Integer customerId;
}
