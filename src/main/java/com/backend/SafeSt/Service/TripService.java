package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Location;
import com.backend.SafeSt.Entity.Trip;
import com.backend.SafeSt.Mapper.TripMapper;
import com.backend.SafeSt.Model.TripModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Repository.TripRepository;
import com.backend.SafeSt.Request.TripReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final LocationRepository locationRepository;
    private final CustomerRepository customerRepository;
    private final TripMapper tripMapper;

    @Transactional
    public TripModel createTrip(TripReq req) throws Exception {
        if (!(Validation.validateDouble(req.getSourceLongitude(), req.getSourceLatitude(), req.getDestinationLongitude(), req.getDestinationLatitude()))) {
            throw new Exception("Source and Destination couldn't be empty");
        }
        var trip = Trip.builder()
                .sourceLongitude(req.getSourceLongitude())
                .sourceLatitude(req.getSourceLatitude())
                .destinationLongitude(req.getDestinationLongitude())
                .destinationLatitude(req.getDestinationLatitude())
                .estimatedTime(req.getEstimatedTime())
                .ended(false)
                .build();
        long period = ChronoUnit.MINUTES.between(LocalDateTime.now(), req.getEstimatedTime().toLocalDateTime());
        period = (long) (period * 1.5);
        trip.setTotalTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(period)));
        Optional<Customer> c = customerRepository.findById(req.getCustomerId());
        if (c.isEmpty()) {
            throw new Exception("Customer not Found");
        }
        trip.setCustomer(c.get());
        tripRepository.save(trip);
        return tripMapper.convertEntityToModel(trip);
    }
}
