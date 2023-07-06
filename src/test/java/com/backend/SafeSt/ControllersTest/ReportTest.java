package com.backend.SafeSt.ControllersTest;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.EmergencyInfoReq;
import com.backend.SafeSt.Request.ReportReq;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @LocalServerPort
    int randomServerPort;
    JSONObject personJsonObject;
    HttpHeaders headers;
    private RestTemplate restTemplate;
    private String url;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" + randomServerPort + "/report/";
        personJsonObject = new JSONObject();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        customerRepository.deleteAll();
        locationRepository.deleteAll();
    }

    public JsonNode addUser() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String url = "http://localhost:" + randomServerPort + "/auth/";
        restTemplate.postForObject(url + "register", request, String.class);
        Customer c = customerRepository.findByEmail("mahmoud.mohamedgamal1@gmail.com").get();
        c.setEnabled(true);
        customerRepository.save(c);
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        return objectMapper.readTree(personResultAsJsonStr);
    }

    @Test
    public void AddReportTest1() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addReport", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Created Successfully\"", response.path("message").toString());
        assertNotNull(response.path("data").path("id"));
        assertEquals("\"Robbery\"", response.path("data").path("category").toString());
        assertEquals("1.0", response.path("data").path("score").toString());
        assertEquals("\"OK\"", response.path("statusCode").toString());
    }

    @Test
    public void AddReportTest2() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addReport", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Fields couldn't be empty\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void AddReportTest3() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "addReport", HttpMethod.POST, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void AddReportTest4() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(1010)
                .longitude("33.2522")
                .latitude("32.5448")
                .category("")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addReport", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Authentication Error\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }


    @Test
    public void ListLocationReportsTest1() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addReport", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "listLocationReports?id=" + node.path("data").path("id").toString() +
                "&longitude=33.251&latitude=32.555", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"No Reports Found\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void ListLocationReportsTest2() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Fire")
                .address("new cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emergencyReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String url1 = "http://localhost:" + randomServerPort + "/emergency/";
        restTemplate.postForObject(url1 + "fireEmergency", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "listLocationReports?id=" + node.path("data").path("id").toString() +
                "&longitude=33.251&latitude=32.555", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"No Reports Found\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void ListLocationReportsTest3() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addReport", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "listLocationReports?id=" + node.path("data").path("id").toString() +
                "&longitude=33.252&latitude=32.544", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Executed Successfully\"", res.path("message").toString());
        assertNotNull(res.path("data").get(0));
        assertEquals("\"OK\"", res.path("statusCode").toString());
    }

    @Test
    public void ListLocationReportsTest4() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addReport", request, String.class);
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        request = new HttpEntity<>(null, headers);
        try {
            restTemplate.exchange(url +
                    "listLocationReports?id" + node.path("data").path("id").toString() +
                    "?longitude=33.251?latitude=32.555", HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void ListLocationReportsTest5() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Fire")
                .address("new cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emergencyReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        new HttpEntity<>(json, headers);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "listLocationReports?id=1010" + "&longitude=33.251&latitude=32.555", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void ListAllLocationWithScoreTest1() throws Exception {
        JsonNode node = addUser();
        var reportReq = ReportReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .reportText("Report1")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reportReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addReport", request, String.class);
        ResponseEntity<String> response = restTemplate.exchange(url + "listAllLocationWithScore?id=" + node.path("data").path("id").toString(), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Executed Successfully\"", res.path("message").toString());
        assertNotNull(res.path("data").get(0));
        assertEquals("\"OK\"", res.path("statusCode").toString());
    }

    @Test
    public void ListAllLocationWithScoreTest2() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "listAllLocationWithScore?id=" + node.path("data").path("id").toString(), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"No Reports Found\"", res.path("message").toString());
        assertNull(res.path("data").get(0));
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void ListAllLocationWithScoreTest3() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        try {
            restTemplate.exchange(url + "listAllLocationWithScore?id=" + node.path("data").path("id").toString(), HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void ListAllLocationWithScoreTest4() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "listAllLocationWithScore?id=1010", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }
}
