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
    @GetMapping(value = "/checkTrip")
    public MainResponse checkTrip(@RequestBody TripReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    tripService.checkTrip(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
