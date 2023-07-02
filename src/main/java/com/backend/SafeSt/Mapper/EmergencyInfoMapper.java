package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.EmergencyInfo;
import com.backend.SafeSt.Model.EmergencyInfoModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class EmergencyInfoMapper {
    public EmergencyInfoModel convertEntityToModel(EmergencyInfo emergencyInfo) {
        String [] date = emergencyInfo.getDate().toString().split(" ");
        return EmergencyInfoModel.builder()
                .id(emergencyInfo.getId())
                .date(date[0])
                .time(date[1])
                .category(emergencyInfo.getCategory().toString())
                .customerId(emergencyInfo.getCustomer().getId())
                .reportId(emergencyInfo.getReport().getId())
                .locationId(emergencyInfo.getLocation().getId())
                .build();

    }
}
