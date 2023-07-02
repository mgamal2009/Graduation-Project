package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Trip;
import com.backend.SafeSt.Mapper.TripMapper;
import com.backend.SafeSt.Model.TripModel;
import com.backend.SafeSt.Repository.TripRepository;
import com.backend.SafeSt.Request.TripReq;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Transactional
    public TripModel createTrip(TripReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        ArrayList<Trip> list = tripRepository.findAllByCustomer_IdAndAndEnded(c.getId(), false);
        if (!list.isEmpty()) {
            throw new Exception("There is an ingoing trip");
        }
        double sourceLong3 = (Math.floor(req.getSourceLongitude() * 1000) / 1000.0);
        double sourceLat3 = (Math.floor(req.getSourceLatitude() * 1000) / 1000.0);
        double destinationLong3 = (Math.floor(req.getDestinationLongitude() * 1000) / 1000.0);
        double destinationLat3 = (Math.floor(req.getDestinationLatitude() * 1000) / 1000.0);
        ZonedDateTime temp = ZonedDateTime.now(ZoneId.of("Africa/Cairo"));
        var trip = Trip.builder()
                .sourceLongitude(sourceLong3)
                .sourceLatitude(sourceLat3)
                .destinationLongitude(destinationLong3)
                .destinationLatitude(destinationLat3)
                .estimatedTime(req.getEstimatedTime())
                .startedAt(Timestamp.valueOf(temp.toLocalDateTime()))
                .ended(false)
                .build();
        long period = (long) (req.getEstimatedTime() * 1.5);
        trip.setEstimatedEnd(Timestamp.valueOf(temp.toLocalDateTime().plusMinutes(period)));
        trip.setRemainingTime(period * 60);
        trip.setCustomer(c);
        tripRepository.save(trip);
        return tripMapper.convertEntityToModel(trip);
    }

    @Transactional
    public boolean endTrip(TripReq req, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        var trip = tripRepository.findById(req.getId())
                .orElseThrow(() -> new Exception("Trip not Found"));
        if (trip.isEnded())
            throw new Exception("Trip already ended");
        trip.setEnded(true);
        tripRepository.save(trip);
        return true;
    }

    public boolean cancelTrip(int id, int customerId, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(customerId, auth);
        var trip = tripRepository.findById(id)
                .orElseThrow(() -> new Exception("Trip not Found"));
        if (trip.isEnded()) {
            throw new Exception("Trip already ended");
        }
        tripRepository.delete(trip);
        return true;
    }

    public TripModel checkIngoingTrip(int id, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(id, auth);
        ArrayList<Trip> list = tripRepository.findAllByCustomer_IdAndAndEnded(id, false);
        if (list.isEmpty()) {
            return null;
        }
        
        long seconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(ZoneId.of("Africa/Cairo")).toLocalDateTime(), list.get(0).getEstimatedEnd().toLocalDateTime());
        if (seconds < 0) {
            throw new Exception("Time ended Are you Ok?");
        }
        list.get(0).setRemainingTime(seconds);
        Trip t = tripRepository.save(list.get(0));
        return tripMapper.convertEntityToModel(t);
    }

    public TripModel extendTrip(TripReq req, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        var trip = tripRepository.findByCustomer_IdAndId(req.getCustomerId(), req.getId())
                .orElseThrow(() -> new Exception("Trip not Found"));
        
        long dif = ChronoUnit.SECONDS.between(ZonedDateTime.now(ZoneId.of("Africa/Cairo")).toLocalDateTime(), trip.getEstimatedEnd().toLocalDateTime());
        if (dif < 0) {
            dif *= -1;
            req.setAddMin((int) (dif + (req.getAddMin() * 60)));
        }
        trip.setEstimatedEnd(Timestamp.valueOf(trip.getEstimatedEnd().toLocalDateTime().plusSeconds(req.getAddMin())));
        long seconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(ZoneId.of("Africa/Cairo")).toLocalDateTime(), trip.getEstimatedEnd().toLocalDateTime());
        trip.setRemainingTime(seconds);
        tripRepository.save(trip);
        return tripMapper.convertEntityToModel(trip);
    }
}
