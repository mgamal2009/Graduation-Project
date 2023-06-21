package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.TrustedContactReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.CustomerService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(value = "/addTrustedContact")
    public MainResponse addTrustedContact(@RequestBody TrustedContactReq req , Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    customerService.addTrustedContact(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @PostMapping(value = "/deleteTrustedContact")
    public MainResponse deleteTrustedContact(@RequestBody TrustedContactReq req , Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.DELETED,
                    customerService.deleteTrustedContact(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @PutMapping(value = "/updatePersonalInfo")
    public MainResponse updatePersonalInfo(@RequestBody CustomerReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.UPDATED,
                    customerService.updatePersonalInfo(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @GetMapping(value = "/getPersonalInfo")
    public MainResponse getPersonalInfo(@RequestBody CustomerReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    customerService.getPersonalInfo(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @GetMapping(value = "/getTrustedInfo")
    public MainResponse getTrustedInfo(@RequestBody TrustedContactReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    customerService.getTrustedInfo(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }



}
