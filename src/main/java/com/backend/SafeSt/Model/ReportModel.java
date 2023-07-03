package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ReportModel {
    private Integer id;
    private String reportText;
    private String category;
    private double score;
    private String date;
    private String time;
    private Integer locationId;
    private Integer customerId;
    private String firstName;
    private String lastName;
}
