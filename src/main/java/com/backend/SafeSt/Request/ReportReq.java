package com.backend.SafeSt.Request;

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
    private Timestamp date;
    private Integer locationId;
    private Integer customerId;
}
