package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.EmergencyInfo;
import com.backend.SafeSt.Model.EmergencyInfoModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class EmergencyInfoMapper {
    public EmergencyInfoModel convertEntityToModel(EmergencyInfo emergencyInfo) {
        return EmergencyInfoModel.builder()
                .id(emergencyInfo.getId())
                .date(emergencyInfo.getDate())
                .emergencyId(emergencyInfo.getEmergency().getId())
                .customerId(emergencyInfo.getCustomer().getId())
                .reportId(emergencyInfo.getReport().getId())
                .categoryId(emergencyInfo.getCategory().getId())
                .locationId(emergencyInfo.getLocation().getId())
                .build();

    }
}
