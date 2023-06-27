package com.backend.SafeSt.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportReq {
    private Integer id;
    private String reportText;
    private String category;
//    private float score;
//    @JsonDeserialize(using = TimestampDeserializer.class )
//    private Timestamp date;
    private double longitude;
    private double latitude;
//    private Integer locationId;
    private Integer customerId;
}
