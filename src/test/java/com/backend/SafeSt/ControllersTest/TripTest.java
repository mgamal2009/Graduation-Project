package com.backend.SafeSt.ControllersTest;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.TripRepository;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.TripReq;
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

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TripTest {
    @LocalServerPort
    int randomServerPort;

    private RestTemplate restTemplate;
    private String url;
    JSONObject personJsonObject;
    HttpHeaders headers;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TripRepository tripRepository;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" + randomServerPort + "/trip/";
        personJsonObject = new JSONObject();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        customerRepository.deleteAll();
        tripRepository.deleteAll();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public void CreateTripTest1() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(response.path("message").toString(), "\"Created Successfully\"");
        assertNotNull(response.path("data").path("id"));
        assertEquals(node.path("data").path("id"), response.path("data").path("customerId"));
        assertEquals("false", response.path("data").path("ended").toString());
        assertEquals(response.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void CreateTripTest2() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrip", request, String.class);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(response.path("message").toString(), "\"There is an ingoing trip\"");
        assertEquals("null",response.path("data").toString());
        assertEquals(response.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void CreateTripTest3() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "addTrip", HttpMethod.POST, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void EndTripTest1() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrip", request, String.class);
        tripReq.setId(1100);
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "endTrip", HttpMethod.PUT, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Trip not Found\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void EndTripTest2() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "endTrip", HttpMethod.PUT, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals("true", res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void EndTripTest3() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        restTemplate.exchange(url +
                "endTrip", HttpMethod.PUT, request, String.class);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "endTrip", HttpMethod.PUT, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Trip already ended\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void EndTripTest4() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url +
                    "endTrip", HttpMethod.PUT, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }


    @Test
    public void CancelTripTest1() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrip", request, String.class);
        tripReq.setId(1100);
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "cancelTrip?id=" + tripReq.getId() + "&customerId=" + tripReq.getCustomerId(), HttpMethod.DELETE, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Trip not Found\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void CancelTripTest2() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "cancelTrip?id=" + tripReq.getId() + "&customerId=" + tripReq.getCustomerId(), HttpMethod.DELETE, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Deleted Successfully\"");
        assertEquals("true", res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void CancelTripTest3() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        restTemplate.exchange(url +
                "endTrip", HttpMethod.PUT, request, String.class);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "cancelTrip?id=" + tripReq.getId() + "&customerId=" + tripReq.getCustomerId(), HttpMethod.DELETE, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Trip already ended\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void CancelTripTest4() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url +
                    "cancelTrip?id=" + tripReq.getId() + "&customerId=" + tripReq.getCustomerId(), HttpMethod.DELETE, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void CheckInGoingTripTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "checkIngoingTrip?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void CheckInGoingTripTest2() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        restTemplate.exchange(url +
                "endTrip", HttpMethod.PUT, request, String.class);
        json = ow.writeValueAsString(null);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "checkIngoingTrip?id=" + tripReq.getCustomerId(), HttpMethod.GET, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void CheckInGoingTripTest3() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        var trip = tripRepository.findById(tripReq.getId());
        trip.get().setEstimatedEnd(
                Timestamp.valueOf(trip.get().getEstimatedEnd().toLocalDateTime()
                        .minusSeconds(trip.get().getRemainingTime() + 1000)));
        tripRepository.save(trip.get());
        ResponseEntity<String> response = restTemplate.exchange(url +
                "checkIngoingTrip?id=" + tripReq.getCustomerId(), HttpMethod.GET, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Time ended Are you Ok?\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void CheckInGoingTripTest4() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        ResponseEntity<String> response = restTemplate.exchange(url +
                "checkIngoingTrip?id=" + tripReq.getCustomerId(), HttpMethod.GET, request, String.class);
        res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(tripReq.getId(), res.path("data").path("id").asInt());
        assertEquals(tripReq.getEstimatedTime(), res.path("data").path("estimatedTime").asInt());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void CheckInGoingTripTest5() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode res = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(res.path("data").path("id").asInt());
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url +
                    "checkIngoingTrip?id=" + tripReq.getCustomerId(), HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void ExtendTripTest1() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrip", request, String.class);
        tripReq.setId(1100);
        tripReq.setAddMin(5);
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "extendTrip", HttpMethod.PUT, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Trip not Found\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void ExtendTripTest2() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "addTrip", request, String.class);
        JsonNode addedRes = objectMapper.readTree(personResultAsJsonStr);
        tripReq.setId(addedRes.path("data").path("id").asInt());
        tripReq.setAddMin(5);
        json = ow.writeValueAsString(tripReq);
        request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url +
                "extendTrip", HttpMethod.PUT, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(addedRes.path("data").path("id"), res.path("data").path("id"));
        assertEquals(addedRes.path("data").path("customerId"), res.path("data").path("customerId"));
        assertNotEquals(addedRes.path("data").path("remainingTime"), res.path("data").path("remainingTime"));
        assertNotEquals(addedRes.path("data").path("estimatedEndTime"), res.path("data").path("estimatedEndTime"));
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void ExtendTripTest3() throws Exception {
        JsonNode node = addUser();
        var tripReq = TripReq.builder()
                .customerId(node.path("data").path("id").asInt())
                .sourceLatitude(33.2522)
                .sourceLongitude(32.5448)
                .destinationLatitude(31.525)
                .destinationLongitude(32.454)
                .estimatedTime(180)
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tripReq);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrip", request, String.class);
        tripReq.setId(1100);
        tripReq.setAddMin(5);
        json = ow.writeValueAsString(tripReq);
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url +
                    "extendTrip", HttpMethod.PUT, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
}
