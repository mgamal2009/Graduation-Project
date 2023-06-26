package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.EmergencyReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.EmergencyService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("emergency")
@RequiredArgsConstructor
public class EmergencyController {
    private final EmergencyService emergencyService;
    /*@PostMapping(value = "/addEmergency")
    public MainResponse createEmergency(@RequestBody EmergencyReq req) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    emergencyService.createEmergency(req));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }*/
}
