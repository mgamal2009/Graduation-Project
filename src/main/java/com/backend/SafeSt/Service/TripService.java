package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Trip;
import com.backend.SafeSt.Mapper.TripMapper;
import com.backend.SafeSt.Model.TripModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.TripRepository;
import com.backend.SafeSt.Request.TripReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final CustomerRepository customerRepository;
    private final TripMapper tripMapper;

    @Transactional
    public TripModel createTrip(TripReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        double sourceLong3 = (Math.floor(req.getSourceLongitude() * 1000) / 1000.0);
        double sourceLat3 = (Math.floor(req.getSourceLatitude() * 1000) / 1000.0);
        double destinationLong3 = (Math.floor(req.getDestinationLongitude() * 1000) / 1000.0);
        double destinationLat3 = (Math.floor(req.getDestinationLatitude() * 1000) / 1000.0);
        var trip = Trip.builder()
                .sourceLongitude(sourceLong3)
                .sourceLatitude(sourceLat3)
                .destinationLongitude(destinationLong3)
                .destinationLatitude(destinationLat3)
                .estimatedTime(req.getEstimatedTime())
                .ended(false)
                .build();
        long period = ChronoUnit.MINUTES.between(LocalDateTime.now(), req.getEstimatedTime().toLocalDateTime());
        period = (long) (period * 1.5);
        trip.setTotalTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(period)));

        trip.setCustomer(c);
        tripRepository.save(trip);
        return tripMapper.convertEntityToModel(trip);
    }
    @Transactional
    public TripModel endTrip(TripReq req, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        var trip = tripRepository.findById(req.getId())
                .orElseThrow(()-> new Exception("Trip not Found"));
        trip.setEnded(true);
        tripRepository.save(trip);
        return tripMapper.convertEntityToModel(trip);
    }
    public boolean cancelTrip(TripReq req, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        var trip = tripRepository.findById(req.getId())
                .orElseThrow(()-> new Exception("Trip not Found"));
        if (trip.isEnded()){
            throw new Exception("Trip already ended");
        }
        tripRepository.delete(trip);
        return true;
    }

    public TripModel checkIngoingTrip(TripReq req, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        ArrayList<Trip> list = tripRepository.findAllByCustomer_IdAndAndEnded(req.getCustomerId(), false);
        if (list.isEmpty()){
            return null;
        }
        return tripMapper.convertEntityToModel(list.get(0));
    }
    public TripModel extendTrip(TripReq req, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        var trip = tripRepository.findByCustomer_IdAndId(req.getCustomerId(), req.getId())
                .orElseThrow(()-> new Exception("Trip not Found"));
        trip.setTotalTime(Timestamp.valueOf(trip.getTotalTime().toLocalDateTime().plusMinutes(req.getAddMin())));
        tripRepository.save(trip);
        return tripMapper.convertEntityToModel(trip);
    }
}
