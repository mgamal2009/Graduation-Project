package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Request.CustomerLocationReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.CustomerLocationService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customerLocation")
@RequiredArgsConstructor
public class CustomerLocationController {
    private final CustomerLocationService customerLocationService;
    @PostMapping(value = "/updateCustomerLocation")
    public MainResponse updateCustomerLocation(@RequestBody CustomerLocationReq req,Authentication auth) {
    try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.UPDATED,
                    customerLocationService.updateCustomerLocation(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}
