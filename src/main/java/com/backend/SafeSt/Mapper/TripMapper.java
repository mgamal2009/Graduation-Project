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
                .estimatedTime(trip.getEstimatedTime().toString())
                .totalTime(trip.getTotalTime().toString())
                .ended(trip.isEnded())
                .sourceLongitude(trip.getSourceLongitude())
                .sourceLatitude(trip.getSourceLatitude())
                .destinationLongitude(trip.getDestinationLongitude())
                .destinationLatitude(trip.getDestinationLatitude())
                .customerId(trip.getCustomer().getId())
                .build();
    }
}
