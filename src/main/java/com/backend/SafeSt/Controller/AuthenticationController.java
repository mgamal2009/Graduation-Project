package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.ResetPasswordReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Service.CustomerService;
import com.backend.SafeSt.Util.ResponseMessage;
import com.sun.mail.smtp.SMTPSendFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    //done
    @PostMapping("/register")
    public MainResponse register(@RequestBody CustomerReq request) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    authenticationService.register(request));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    //done
    @PostMapping("/authenticate")
    public MainResponse authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    authenticationService.authenticate(request));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    //done
    @GetMapping(value = "/confirm-account")
    public MainResponse confirmMail(@RequestParam String urlToken) {
        try {
            return authenticationService.confirmMail(urlToken);
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }


    /*@GetMapping(value = "/resend-confirmation-email")
    public MainResponse resendConfirmationEmail(@RequestParam String urlToken) {
        try {
            return authenticationService.resendConfirmationEmail(urlToken);
        } catch (Exception e) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }*/

    /*@GetMapping(value = "/reset-password")
    public MainResponse sendResetMail(@RequestParam String email) {
        try {
            return authenticationService.sendResetPasswordMail(email);
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @PostMapping(value = "/update-password")
    public MainResponse updatePassword(@RequestBody ResetPasswordReq req) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.UPDATED,
                    authenticationService.updatePassword(req));
        } catch (Exception e) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }*/
}
