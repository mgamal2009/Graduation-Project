package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Trip;
import com.backend.SafeSt.Model.TripModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TripMapper {
    public TripModel convertEntityToModel(Trip trip) {
        String [] startDate = trip.getStartedAt().toString().split(" ");
        String [] estDate = trip.getEstimatedEnd().toString().split(" ");

        return TripModel.builder()
                .id(trip.getId())
                .startedAtDate(startDate[0])
                .startedAtTime(startDate[1].replaceAll(':','-')
                .estimatedEndDate(estDate[0])
                .estimatedEndTime(estDate[1].replaceAll(':','-')
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
