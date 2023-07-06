package com.backend.SafeSt.ServicesTest;

import com.backend.SafeSt.Model.LocationModel;
import com.backend.SafeSt.Model.ReportModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Repository.ReportRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.EmergencyInfoReq;
import com.backend.SafeSt.Request.ReportReq;
import com.backend.SafeSt.Service.AuthenticationService;
import com.backend.SafeSt.Service.EmergencyService;
import com.backend.SafeSt.Service.ReportService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class ReportTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private EmergencyService emergencyService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;
    private CustomerReq req;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @BeforeEach
    public void clear() {
        customerRepository.deleteAll();
        reportRepository.deleteAll();
        locationRepository.deleteAll();
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
    public void AddReportTest1() throws Exception {
        Authentication auth = addUser();
        var reportReq = ReportReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ReportModel model = reportService.addReport(reportReq, auth);
        assertNotNull(model.getId());
        assertEquals("Robbery", model.getCategory());
        assertEquals(1.0, model.getScore());
    }

    @Test
    public void AddReportTest2() throws Exception {
        Authentication auth = addUser();
        var reportReq = ReportReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("")
                .reportText("Report1")
                .build();
        Exception exception = assertThrows(Exception.class, () -> reportService.addReport(reportReq, auth));
        assertEquals("Fields couldn't be empty", exception.getMessage());
    }

    @Test
    public void ListLocationReportsTest1() throws Exception {
        Authentication auth = addUser();
        var reportReq = ReportReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        reportService.addReport(reportReq, auth);
        Exception exception = assertThrows(Exception.class, () -> reportService.listLocationReports(req.getId(), "33.251", "32.555", auth));
        assertEquals("No Reports Found", exception.getMessage());
    }

    @Test
    public void ListLocationReportsTest2() throws Exception {
        Authentication auth = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Fire")
                .address("new cairo")
                .build();
        emergencyService.createEmergency(emergencyReq, auth);
        Exception exception = assertThrows(Exception.class, () -> reportService.listLocationReports(req.getId(), "33.252", "32.544", auth));
        assertEquals("No Reports Found", exception.getMessage());
    }

    @Test
    public void ListLocationReportsTest3() throws Exception {
        Authentication auth = addUser();
        var reportReq = ReportReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        reportService.addReport(reportReq, auth);
        List<ReportModel> list = reportService.listLocationReports(req.getId(), "33.252", "32.544", auth);
        assertEquals(1, list.size());
    }

    @Test
    public void ListAllLocationWithScoreTest1() throws Exception {
        Authentication auth = addUser();
        var reportReq = ReportReq.builder()
                .customerId(req.getId())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        reportService.addReport(reportReq, auth);
        List<LocationModel> list = reportService.listAllLocationWithScore(req.getId(), auth);
        assertEquals(1, list.size());
    }

    @Test
    public void ListAllLocationWithScoreTest2() throws Exception {
        Authentication auth = addUser();
        Exception exception = assertThrows(Exception.class, () -> reportService.listAllLocationWithScore(req.getId(), auth));
        assertEquals("No Reports Found", exception.getMessage());
    }
}
