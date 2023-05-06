package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class EmergencyInfoModel {
    private Integer id;
    private Timestamp date;
    private Integer emergencyId;
    private Integer customerId;
    private Integer categoryId;
    private Integer locationId;
    private Integer reportId;
}
