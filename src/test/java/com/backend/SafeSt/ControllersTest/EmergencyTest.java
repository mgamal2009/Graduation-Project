package com.backend.SafeSt.ControllersTest;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.EmergencyInfoReq;
import com.backend.SafeSt.Request.TrustedContactReq;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmergencyTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @LocalServerPort
    int randomServerPort;
    JSONObject personJsonObject;
    HttpHeaders headers;
    private RestTemplate restTemplate;
    private String url;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" + randomServerPort + "/emergency/";
        personJsonObject = new JSONObject();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        customerRepository.deleteAll();
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

    public void addAnotherUserAndEnabled() throws Exception {
        String username = "mmge2009@yahoo.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String url = "http://localhost:" + randomServerPort + "/auth/";
        restTemplate.postForObject(url + "register", request, String.class);
        Customer c = customerRepository.findByEmail(username).get();
        c.setEnabled(true);
        customerRepository.save(c);
    }

    @Test
    public void CreateEmergencyTest1() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Fire")
                .address("new Cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        addAnotherUserAndEnabled();
        String url1 = "http://localhost:" + randomServerPort + "/customer/";
        var trustedReq = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(trustedReq);
        String token = node.path("data").path("token").toString();
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        restTemplate.postForObject(url1 + "addTrustedContact", request, String.class);
        json = ow.writeValueAsString(emergencyReq);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "fireEmergency", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Created Successfully\"", response.path("message").toString());
        assertEquals("null", response.path("data").path("reportId").toString());
        assertEquals("\"OK\"", response.path("statusCode").toString());
    }

    @Test
    public void CreateEmergencyTest2() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .address("New Cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        addAnotherUserAndEnabled();
        String url1 = "http://localhost:" + randomServerPort + "/customer/";
        var trustedReq = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(trustedReq);
        String token = node.path("data").path("token").toString();
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        restTemplate.postForObject(url1 + "addTrustedContact", request, String.class);
        json = ow.writeValueAsString(emergencyReq);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "fireEmergency", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Created Successfully\"", response.path("message").toString());
        assertNotNull(response.path("data").path("reportId"));
        assertEquals("\"OK\"", response.path("statusCode").toString());
    }

    @Test
    public void CreateEmergencyTest3() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("")
                .address("New Cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emergencyReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "fireEmergency", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Fields couldn't be empty\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void CreateEmergencyTest4() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .longitude("33.2522")
                .latitude("32.5448")
                .category("Robbery")
                .address("New Cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emergencyReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "fireEmergency", HttpMethod.POST, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void CreateEmergencyTest5() throws Exception {
        JsonNode node = addUser();
        var emergencyReq = EmergencyInfoReq.builder()
                .customerId(1010)
                .longitude("33.2522")
                .latitude("32.5448")
                .category("")
                .address("New Cairo")
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emergencyReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "fireEmergency", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Authentication Error\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }
}
