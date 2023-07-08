package com.backend.SafeSt.ServicesTest;

import com.backend.SafeSt.Entity.ConfirmationToken;
import com.backend.SafeSt.Repository.ConfirmationTokenRepository;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Response.AuthenticationResponse;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Util.RSAUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class AuthenticationTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @BeforeEach
    public void clear() {
        customerRepository.deleteAll();
    }

    @Test
    public void RegisterTest1() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        assertTrue(authenticationService.register(req));
    }

    @Test
    public void RegisterTest2() {
        var req = CustomerReq.builder().firstname("").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        Exception thrown = assertThrows(Exception.class, () -> authenticationService.register(req));
        assertEquals("Fields couldn't be empty", thrown.getMessage());
    }

    @Test
    public void RegisterTest3() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        Exception thrown = assertThrows(Exception.class, () -> authenticationService.register(req));
        assertEquals("This Email is already used", thrown.getMessage());
    }

    @Test
    public void RegisterTest4() {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("123").confirmationPassword("1234").phoneNumber("01012").build();
        Exception thrown = assertThrows(Exception.class, () -> authenticationService.register(req));
        assertEquals("Password and Confirmation Password Should be the Same!!", thrown.getMessage());
    }


    @Test
    public void LoginTest1() {
        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(AuthenticationRequest.builder().email("mahmoud.mohamedgamal1@gmail.com").password("1234").build()), "Bad Credentials");
    }

    @Test
    public void LoginTest2() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        assertThrows(Exception.class, () -> authenticationService.authenticate(AuthenticationRequest.builder().email("mahmoud.mohamedgamal1@gmail.com").password("1234").build()), "Please Confirm your Account First!!");
    }

    @Test
    public void LoginTest3() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail("mahmoud.mohamedgamal1@gmail.com");
        customer.get().setEnabled(true);
        customerRepository.save(customer.get());
        AuthenticationResponse res = authenticationService.authenticate(AuthenticationRequest.builder().email("mahmoud.mohamedgamal1@gmail.com").password("1234").build());
        assertEquals(customer.get().getId(), res.getId());
    }

    @Test
    public void ConfirmAccount1() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        String token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId()).getConfirmationToken();
        token = token.concat("d5");
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token));
        Exception exception = assertThrows(Exception.class, () -> authenticationService.confirmMail(encryptedToken));
        assertEquals("Invalid Link!", exception.getMessage());
    }

    @Test
    public void ConfirmAccount2() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        String token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId()).getConfirmationToken();
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token));
        customer.get().setEnabled(true);
        customerRepository.save(customer.get());
        MainResponse res = authenticationService.confirmMail(encryptedToken);
        assertEquals("Your Account is  Already Confirmed!", res.getMessage());
    }

    @Test
    public void ConfirmAccount3() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        ConfirmationToken token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId());
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token.getConfirmationToken()));
        token.setCreatedDate(token.getCreatedDate().minusMinutes(65));
        confirmationTokenRepository.save(token);
        Exception exception = assertThrows(Exception.class, () -> authenticationService.confirmMail(encryptedToken));
        assertEquals("Link is Expired! New Link Was Sent to Your Email.", exception.getMessage());
    }

    @Test
    public void ConfirmAccount4() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        ConfirmationToken token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId());
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token.getConfirmationToken()));
        MainResponse response = authenticationService.confirmMail(encryptedToken);
        assertEquals("Your Account is Confirmed Successfully!", response.getMessage());
    }

    @Test
    public void LogoutTest1() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(customer.get().getId());
        customerRepository.save(customer.get());
        authenticationService.authenticate(AuthenticationRequest.builder().email(username).password(password).build());
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        assertTrue(authenticationService.logout(req, auth));
    }

    @Test
    public void LogoutTest2() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(1001);
        customerRepository.save(customer.get());
        authenticationService.authenticate(AuthenticationRequest.builder().email(username).password(password).build());
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Exception exception = assertThrows(Exception.class, () -> authenticationService.logout(req, auth));
        assertEquals("Authentication Error", exception.getMessage());
    }
}
