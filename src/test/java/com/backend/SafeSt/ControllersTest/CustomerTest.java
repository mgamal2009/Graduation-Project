package com.backend.SafeSt.ControllersTest;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.CustomerReq;
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
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerTest {
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
        url = "http://localhost:" + randomServerPort + "/customer/";
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
    public void AddTrustedContactTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Email couldn't be empty\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void AddTrustedContactTest2() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Email not found\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void AddTrustedContactTest3() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mahmoud.mohamedgamal1@gmail.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"You can't add your self\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void AddTrustedContactTest4() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Created Successfully\"", response.path("message").toString());
        assertEquals("\"mmge2009@yahoo.com\"", response.path("data").path("email").toString());
        assertEquals("\"OK\"", response.path("statusCode").toString());
    }

    @Test
    public void AddTrustedContactTest5() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Already In Your List\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void AddTrustedContactTest6() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "addTrustedContact", HttpMethod.POST, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void AddTrustedContactTest7() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mahmoud.mohamedgamal1@gmail.com").userId(1010).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Authentication Error\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }

    @Test
    public void DeleteTrustedContactTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id=" + node.path("data").path("id") + "&email=" + "mmge2009@hotmail.com", HttpMethod.DELETE, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Trusted Email not found\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void DeleteTrustedContactTest2() throws Exception {
        JsonNode node = addUser();
        addAnotherUserAndEnabled();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id=" + node.path("data").path("id") + "&email=" + "mmge2009@yahoo.com", HttpMethod.DELETE, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Email not in your Trusted Contacts\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void DeleteTrustedContactTest3() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id=" + node.path("data").path("id") + "&email=" + "mmge2009@yahoo.com", HttpMethod.DELETE, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Deleted Successfully\"", res.path("message").toString());
        assertEquals("true", res.path("data").toString());
        assertEquals("\"OK\"", res.path("statusCode").toString());
    }

    @Test
    public void DeleteTrustedContactTest4() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        try {
            restTemplate.exchange(url + "deleteTrustedContact?id=" + node.path("data").path("id") + "&email=" + "mmge2009@yahoo.com", HttpMethod.DELETE, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void DeleteTrustedContactTest5() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id=1010&email=" + "mmge2009@yahoo.com", HttpMethod.DELETE, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void GetPersonalTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getPersonalInfo?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Executed Successfully\"", res.path("message").toString());
        assertEquals(node.path("data").path("id").asInt(), res.path("data").path("id").asInt());
        assertEquals("\"OK\"", res.path("statusCode").toString() );
    }

    @Test
    public void GetPersonalTest2() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "getPersonalInfo?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void GetPersonalTest3() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getPersonalInfo?id=1010", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void CheckTokenTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "checkTokenAvailability?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Executed Successfully\"" , res.path("message").toString());
        assertEquals("true", res.path("data").toString());
        assertEquals("\"OK\"",res.path("statusCode").toString());
    }

    @Test
    public void CheckTokenTest2() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "checkTokenAvailability?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void CheckTokenTest3() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "checkTokenAvailability?id=1010", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"",res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"",res.path("statusCode").toString());
    }

    @Test
    public void GetNumOfTrustedTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getNumOfTrusted?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Executed Successfully\"",res.path("message").toString());
        assertEquals(1, res.path("data").asInt());
        assertEquals( "\"OK\"",res.path("statusCode").toString());
    }

    @Test
    public void GetNumOfTrustedTest2() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        try {
            restTemplate.exchange(url + "getNumOfTrusted?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void GetNumOfTrustedTest3() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getNumOfTrusted?id=1010", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"",res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void GetAllTrustedTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getAllTrusted?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Executed Successfully\"", res.path("message").toString() );
        assertEquals("\"mmge2009@yahoo.com\"", res.path("data").get(0).path("email").toString());
        assertEquals("\"OK\"", res.path("statusCode").toString() );
    }

    @Test
    public void GetAllTrustedTest2() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getAllTrusted?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"User Don't have Trusted Contacts\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void GetAllTrustedTest3() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        try {
            restTemplate.exchange(url + "getAllTrusted?id=" + node.path("data").path("id"), HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }
    @Test
    public void GetAllTrustedTest4() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getAllTrusted?id=1010", HttpMethod.GET, request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals("\"Authentication Error\"", res.path("message").toString());
        assertEquals("null", res.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", res.path("statusCode").toString());
    }

    @Test
    public void SetVoiceTest1() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "setVoice?saved=0&id=" + node.path("data").path("id"), request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Executed Successfully\"", response.path("message").toString());
        assertEquals( "false", response.path("data").toString());
        assertEquals("\"OK\"", response.path("statusCode").toString());
    }

    @Test
    public void SetVoiceTest2() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "setVoice?saved=1&id=" + node.path("data").path("id"), request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals( "\"Executed Successfully\"", response.path("message").toString());
        assertEquals("true", response.path("data").toString());
        assertEquals("\"OK\"", response.path("statusCode").toString());
    }

    @Test
    public void SetVoiceTest3() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString() + "5d";
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "setVoice?saved=1?id=" + node.path("data").path("id"), HttpMethod.POST, request, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals("403 FORBIDDEN", e.getStatusCode().toString());
        }
    }

    @Test
    public void SetVoiceTest4() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "setVoice?saved=1&id=1010" , request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals( "\"Authentication Error\"", response.path("message").toString());
        assertEquals("null", response.path("data").toString());
        assertEquals("\"INTERNAL_SERVER_ERROR\"", response.path("statusCode").toString());
    }
}
