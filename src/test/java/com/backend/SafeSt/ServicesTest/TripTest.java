package com.backend.SafeSt.ServicesTest;

import com.backend.SafeSt.Model.TripModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.TripRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.TripReq;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class TripTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TripService tripService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;
    private CustomerReq req;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @BeforeEach
    public void clear() {
        customerRepository.deleteAll();
        tripRepository.deleteAll();
    }

    public Authentication addUser() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(customer.get().getId());
        customerRepository.save(customer.get());
        authenticationService.authenticate(AuthenticationRequest.builder().email(username).password(password).build());
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Test
    public void CreateTripTest1() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel model = tripService.createTrip(tripReq, auth);
        assertNotNull(model.getId());
        assertEquals(req.getId(), model.getCustomerId());
        assertFalse(model.isEnded());
    }

    @Test
    public void CreateTripTest2() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        tripService.createTrip(tripReq, auth);
        Exception exception = assertThrows(Exception.class, () -> tripService.createTrip(tripReq, auth));
        assertEquals("There is an ingoing trip", exception.getMessage());
    }

    @Test
    public void EndTripTest1() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        tripService.createTrip(tripReq, auth);
        tripReq.setId(1100);
        Exception exception = assertThrows(Exception.class, () -> tripService.endTrip(tripReq, auth));
        assertEquals("Trip not Found", exception.getMessage());
    }

    @Test
    public void EndTripTest2() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel model = tripService.createTrip(tripReq, auth);
        tripReq.setId(model.getId());
        boolean ended = tripService.endTrip(tripReq, auth);
        assertTrue(ended);
    }

    @Test
    public void EndTripTest3() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel model = tripService.createTrip(tripReq, auth);
        tripReq.setId(model.getId());
        boolean ended = tripService.endTrip(tripReq, auth);
        assertTrue(ended);
        Exception exception = assertThrows(Exception.class, () -> tripService.endTrip(tripReq, auth));
        assertEquals("Trip already ended", exception.getMessage());
    }


    @Test
    public void CancelTripTest1() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        tripService.createTrip(tripReq, auth);
        tripReq.setId(1100);
        Exception exception = assertThrows(Exception.class, () -> tripService.cancelTrip(tripReq.getId(), tripReq.getCustomerId(), auth));
        assertEquals("Trip not Found", exception.getMessage());
    }

    @Test
    public void CancelTripTest2() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel model = tripService.createTrip(tripReq, auth);
        tripReq.setId(model.getId());
        boolean cancelled = tripService.cancelTrip(tripReq.getId(), tripReq.getCustomerId(), auth);
        assertTrue(cancelled);
    }

    @Test
    public void CancelTripTest3() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel model = tripService.createTrip(tripReq, auth);
        tripReq.setId(model.getId());
        boolean ended = tripService.endTrip(tripReq, auth);
        assertTrue(ended);
        Exception exception = assertThrows(Exception.class, () -> tripService.cancelTrip(tripReq.getId(), tripReq.getCustomerId(), auth));
        assertEquals("Trip already ended", exception.getMessage());
    }

    @Test
    public void CheckInGoingTripTest1() throws Exception {
        Authentication auth = addUser();
        TripModel model = tripService.checkIngoingTrip(req.getId(), auth);
        assertNull(model);
    }

    @Test
    public void CheckInGoingTripTest2() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel modelAdded = tripService.createTrip(tripReq, auth);
        tripReq.setId(modelAdded.getId());
        tripService.endTrip(tripReq, auth);
        TripModel model = tripService.checkIngoingTrip(req.getId(), auth);
        assertNull(model);
    }

    @Test
    public void CheckInGoingTripTest3() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel modelAdded = tripService.createTrip(tripReq, auth);
        tripReq.setId(modelAdded.getId());
        var trip = tripRepository.findById(modelAdded.getId());
        trip.get().setEstimatedEnd(
                Timestamp.valueOf(trip.get().getEstimatedEnd().toLocalDateTime()
                        .minusSeconds(trip.get().getRemainingTime() + 1000)));
        tripRepository.save(trip.get());
        Exception exception = assertThrows(Exception.class, () -> tripService.checkIngoingTrip(req.getId(), auth));
        assertEquals("Time ended Are you Ok?", exception.getMessage());
    }

    @Test
    public void CheckInGoingTripTest4() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel modelAdded = tripService.createTrip(tripReq, auth);
        tripReq.setId(modelAdded.getId());
        TripModel model = tripService.checkIngoingTrip(req.getId(), auth);
        assertEquals(req.getId(), model.getCustomerId());
        assertEquals(tripReq.getEstimatedTime(), model.getEstimatedTime());
    }

    @Test
    public void ExtendTripTest1() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        tripService.createTrip(tripReq, auth);
        tripReq.setId(1010);
        tripReq.setAddMin(5);
        Exception exception = assertThrows(Exception.class, () -> tripService.extendTrip(tripReq, auth));
        assertEquals("Trip not Found", exception.getMessage());
    }

    @Test
    public void ExtendTripTest2() throws Exception {
        Authentication auth = addUser();
        var tripReq = TripReq.builder()
                .customerId(req.getId())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        TripModel modelAdded = tripService.createTrip(tripReq, auth);
        tripReq.setId(modelAdded.getId());
        tripReq.setAddMin(5);
        TripModel model = tripService.extendTrip(tripReq, auth);
        assertEquals(modelAdded.getId(), model.getId());
        assertEquals(modelAdded.getCustomerId(), model.getCustomerId());
        assertNotEquals(modelAdded.getRemainingTime(), model.getRemainingTime());
        assertNotEquals(modelAdded.getEstimatedEndTime(), model.getEstimatedEndTime());

    }
}
