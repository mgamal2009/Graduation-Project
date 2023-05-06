package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Emergency;
import com.backend.SafeSt.Model.EmergencyModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class EmergencyMapper {
    public EmergencyModel convertEntityToModel(Emergency emergency){
        return EmergencyModel.builder()
                .id(emergency.getId())
                .categoryId(emergency.getCategory().getId())
                .build();
    }
}
