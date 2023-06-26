package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.LocationReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.LocationService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    /*@PostMapping(value = "/addLocation")
    public MainResponse createLocation(@RequestBody LocationReq req) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    locationService.createLocation(req));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }*/
}
