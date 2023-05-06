package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ReportModel {
    private Integer id;
    private String reportText;
    private float score;
    private Timestamp date;
    private Integer locationId;
    private Integer customerId;
}
