package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public MainResponse register(@RequestBody CustomerReq request){
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    authenticationService.register(request));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @PostMapping("/authenticate")
    public MainResponse authenticate(@RequestBody AuthenticationRequest request){
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    authenticationService.authenticate(request));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
