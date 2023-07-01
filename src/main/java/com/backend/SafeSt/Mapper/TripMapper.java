package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Trip;
import com.backend.SafeSt.Model.TripModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TripMapper {
    public TripModel convertEntityToModel(Trip trip) {
        return TripModel.builder()
                .id(trip.getId())
                .startedAt(trip.getStartedAt().toString().replace(' ','@'))
                .estimatedEnd(trip.getEstimatedEnd().toString().replace(' ','@'))
                .estimatedTime(trip.getEstimatedTime())
                .remainingTime(trip.getRemainingTime())
                .ended(trip.isEnded())
                .sourceLongitude(trip.getSourceLongitude())
                .sourceLatitude(trip.getSourceLatitude())
                .destinationLongitude(trip.getDestinationLongitude())
                .destinationLatitude(trip.getDestinationLatitude())
                .customerId(trip.getCustomer().getId())
                .build();
    }
}
