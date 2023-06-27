/*
package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Location;
import com.backend.SafeSt.Mapper.LocationMapper;
import com.backend.SafeSt.Model.LocationModel;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Request.LocationReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;
    @Transactional
    public LocationModel createLocation(LocationReq req) throws Exception  {
        if (!(Validation.validateDouble(req.getLatitude(),req.getLongitude()))){
            throw new Exception("Latitude and Longitude Should be double");
        }
        var location = Location.builder()
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .averageScore(0)
                .build();
        locationRepository.save(location);
        return locationMapper.convertEntityToModel(location);
    }
}
*/
