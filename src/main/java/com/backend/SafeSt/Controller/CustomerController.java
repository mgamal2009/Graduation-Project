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
    
    @DeleteMapping(value = "/deleteTrustedContact")
    public MainResponse deleteTrustedContact(@RequestParam int id,@RequestParam String email, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.DELETED,
                    customerService.deleteTrustedContact(id,email,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    
    /*@GetMapping(value = "/getTrustedInfo")
    public MainResponse getTrustedInfo(@RequestParam int id,@RequestParam String email, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    customerService.getTrustedInfo(id,email,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }*/
    
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
    public MainResponse getPersonalInfo(@RequestParam int id, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    customerService.getPersonalInfo(id,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    
    @GetMapping(value = "/checkTokenAvailability")
    public MainResponse checkTokenAvailability(@RequestParam int id, Authentication auth) {
        try {
            CustomerService.checkLoggedIn(id,auth);
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    true);
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @GetMapping(value = "/getNumOfTrusted")
    public MainResponse getNumOfTrusted(@RequestParam int id, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    customerService.getNumOfTrusted(id,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    
    @GetMapping(value = "/getAllTrusted")
    public MainResponse getAllTrustedContacts(@RequestParam int id, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    customerService.getAllTrustedContacts(id,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }


}
