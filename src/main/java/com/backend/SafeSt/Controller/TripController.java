package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.TripReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.TripService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trip")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    //done
    @PostMapping(value = "/addTrip")
    public MainResponse createTrip(@RequestBody TripReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    tripService.createTrip(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    //done
    @PutMapping(value = "/endTrip")
    public MainResponse endTrip(@RequestBody TripReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    tripService.endTrip(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    //done
    @DeleteMapping(value = "/cancelTrip")
    public MainResponse cancelTrip(@RequestBody TripReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.DELETED,
                    tripService.cancelTrip(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    //done
    @GetMapping(value = "/checkIngoingTrip")
    public MainResponse checkIngoingTrip(@RequestBody TripReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    tripService.checkIngoingTrip(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    //done
    @PutMapping(value = "/extendTrip")
    public MainResponse extendTrip(@RequestBody TripReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    tripService.extendTrip(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
