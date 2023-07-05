package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EmergencyInfoModel {
    private Integer id;
    private String date;
    private String time;
    private String address;
    private String category;
    private Integer customerId;
    private Integer locationId;
    private Integer reportId;
}
