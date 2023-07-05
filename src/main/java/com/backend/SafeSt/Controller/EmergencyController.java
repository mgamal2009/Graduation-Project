package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.EmergencyInfoReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.EmergencyService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("emergency")
@RequiredArgsConstructor
public class EmergencyController {
    private final EmergencyService emergencyService;

    @PostMapping(value = "/fireEmergency")
    public MainResponse createEmergency(@RequestBody EmergencyInfoReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    emergencyService.createEmergency(req, auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
