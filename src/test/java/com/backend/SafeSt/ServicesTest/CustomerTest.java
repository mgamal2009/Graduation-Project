package com.backend.SafeSt.ServicesTest;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Model.CustomerModel;
import com.backend.SafeSt.Model.TrustedContactModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.TrustedContactReq;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class CustomerTest {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private CustomerReq req;

    @BeforeEach
    public void clear() {
        customerRepository.deleteAll();
    }

    public Authentication addUser() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(req);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(customer.get().getId());
        customerRepository.save(customer.get());
        authenticationService.authenticate(AuthenticationRequest.builder().email(username).password(password).build());
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Test
    public void CheckLoggedInTest1() throws Exception {
        Authentication auth = addUser();
        Customer c = CustomerService.checkLoggedIn(req.getId(), auth);
        assertEquals(req.getId(), c.getId());
    }

    @Test
    public void CheckLoggedInTest2() throws Exception {
        Authentication auth = addUser();
        Exception exception = assertThrows(Exception.class, () -> CustomerService.checkLoggedIn(1006, auth));
        assertEquals("Authentication Error", exception.getMessage());
    }

    @Test
    public void AddTrustedContactTest1() throws Exception {
        Authentication auth = addUser();
        var trustedReq = TrustedContactReq.builder().email("").userId(req.getId()).build();
        Exception exception = assertThrows(Exception.class, () -> customerService.addTrustedContact(trustedReq, auth));
        assertEquals("Email couldn't be empty", exception.getMessage());
    }

    @Test
    public void AddTrustedContactTest2() throws Exception {
        Authentication auth = addUser();
        var trustedReq = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(req.getId()).build();
        Exception exception = assertThrows(Exception.class, () -> customerService.addTrustedContact(trustedReq, auth));
        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    public void AddTrustedContactTest3() throws Exception {
        Authentication auth = addUser();
        var trustedReq = TrustedContactReq.builder().email("mahmoud.mohamedgamal1@gmail.com").userId(req.getId()).build();
        Exception exception = assertThrows(Exception.class, () -> customerService.addTrustedContact(trustedReq, auth));
        assertEquals("You can't add your self", exception.getMessage());
    }

    @Test
    public void AddTrustedContactTest4() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var trustedReq = TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        TrustedContactModel model = customerService.addTrustedContact(trustedReq, auth);
        assertEquals(username, model.getEmail());
    }

    @Test
    public void AddTrustedContactTest5() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var trustedReq = TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        customerService.addTrustedContact(trustedReq, auth);
        Exception exception = assertThrows(Exception.class, () -> customerService.addTrustedContact(trustedReq, auth));
        assertEquals("Already In Your List", exception.getMessage());
    }

    @Test
    public void DeleteTrustedContactTest1() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var trustedReq = TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        customerService.addTrustedContact(trustedReq, auth);
        Exception exception = assertThrows(Exception.class, () -> customerService.deleteTrustedContact(req.getId(), "mmge2009@hotmail.com", auth));
        assertEquals("Trusted Email not found", exception.getMessage());
    }

    @Test
    public void DeleteTrustedContactTest2() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        Exception exception = assertThrows(Exception.class, () -> customerService.deleteTrustedContact(req.getId(), "mmge2009@yahoo.com", auth));
        assertEquals("Email not in your Trusted Contacts", exception.getMessage());
    }

    @Test
    public void DeleteTrustedContactTest3() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var trustedReq = TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        customerService.addTrustedContact(trustedReq, auth);
        boolean deleted = customerService.deleteTrustedContact(req.getId(), username, auth);
        assertTrue(deleted);
    }

    @Test
    public void GetPersonalTest1() throws Exception {
        Authentication auth = addUser();
        CustomerModel model = customerService.getPersonalInfo(req.getId(), auth);
        assertEquals(req.getId(), model.getId());
    }

    @Test
    public void GetNumOfTrustedTest1() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var trustedReq = TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        customerService.addTrustedContact(trustedReq, auth);
        int size = customerService.getNumOfTrusted(req.getId(), auth);
        assertEquals(1, size);
    }

    @Test
    public void GetAllTrustedTest1() throws Exception {
        Authentication auth = addUser();
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var trustedReq = TrustedContactReq.builder().email(username).userId(req.getId()).build();
        CustomerReq customerReq = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        authenticationService.register(customerReq);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        customerReq.setId(customer.get().getId());
        customerRepository.save(customer.get());
        customerService.addTrustedContact(trustedReq, auth);
        ArrayList<TrustedContactModel> list = customerService.getAllTrustedContacts(req.getId(), auth);
        assertEquals(username, list.get(0).getEmail());
    }

    @Test
    public void GetAllTrustedTest2() throws Exception {
        Authentication auth = addUser();
        Exception exception = assertThrows(Exception.class, () -> customerService.getAllTrustedContacts(req.getId(), auth));
        assertEquals("User Don't have Trusted Contacts", exception.getMessage());
    }

    @Test
    public void SetVoiceTest1() throws Exception {
        Authentication auth = addUser();
        boolean done = customerService.setVoice(req.getId(), 0, auth);
        assertFalse(done);
    }

    @Test
    public void SetVoiceTest2() throws Exception {
        Authentication auth = addUser();
        boolean done = customerService.setVoice(req.getId(), 1, auth);
        assertTrue(done);
    }
}
