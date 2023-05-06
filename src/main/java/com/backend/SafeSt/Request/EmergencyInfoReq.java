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
public class EmergencyInfoReq {
    private Integer id;

    private Timestamp date;
    private Integer emergencyId;
    private Integer customerId;
    private Integer categoryId;
    private Integer locationId;
    private Integer reportId;
}
