package com.backend.SafeSt.ServicesTest;

import com.backend.SafeSt.Model.EmergencyInfoModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.EmergencyInfoRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.EmergencyInfoReq;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Service.EmergencyService;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class EmergencyTest {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmergencyInfoRepository emergencyInfoRepository;

    @Autowired
    private EmergencyService emergencyService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private CustomerReq req;

    @BeforeEach
    public void clear() {
        customerRepository.deleteAll();
        emergencyInfoRepository.deleteAll();
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
    public void CreateEmergencyTest1() throws Exception {
        Authentication auth = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Fire")
                .address("new Cairo")
                .build();
        EmergencyInfoModel model = emergencyService.createEmergency(emergencyReq, auth);
        assertNull(model.getReportId());
    }

    @Test
    public void CreateEmergencyTest2() throws Exception {
        Authentication auth = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .address("New Cairo")
                .build();
        EmergencyInfoModel model = emergencyService.createEmergency(emergencyReq, auth);
        assertNotNull(model.getReportId());
    }

    @Test
    public void CreateEmergencyTest3() throws Exception {
        Authentication auth = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("")
                .address("New Cairo")
                .build();
        Exception exception = assertThrows(Exception.class, () -> emergencyService.createEmergency(emergencyReq, auth));
        assertEquals("Fields couldn't be empty", exception.getMessage());
    }
}
