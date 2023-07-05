package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Location;
import com.backend.SafeSt.Model.LocationModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LocationMapper {
    public LocationModel convertEntityToModel(Location location) {
        return LocationModel.builder()
                .id(location.getId())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .averageScore(location.getAverageScore())
                .address(location.getAddress())
                .build();
    }
}
