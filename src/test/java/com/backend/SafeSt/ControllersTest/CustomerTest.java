package com.backend.SafeSt.ControllersTest;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Model.CustomerModel;
import com.backend.SafeSt.Model.TrustedContactModel;
import com.backend.SafeSt.Repository.ConfirmationTokenRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerTest {
    @LocalServerPort
    int randomServerPort;

    private RestTemplate restTemplate;
    private String url;
    JSONObject personJsonObject;
    HttpHeaders headers;
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
        assertEquals(response.path("message").toString(), "\"Email couldn't be empty\"");
        assertEquals(response.path("data").toString(), "null");
        assertEquals(response.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
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
        assertEquals(response.path("message").toString(), "\"Email not found\"");
        assertEquals(response.path("data").toString(), "null");
        assertEquals(response.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
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
        assertEquals(response.path("message").toString(), "\"You can't add your self\"");
        assertEquals(response.path("data").toString(), "null");
        assertEquals(response.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
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
        assertEquals(response.path("message").toString(), "\"Created Successfully\"");
        assertEquals(response.path("data").path("email").toString(), "\"mmge2009@yahoo.com\"");
        assertEquals(response.path("statusCode").toString(), "\"OK\"");
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
        assertEquals(response.path("message").toString(), "\"Already In Your List\"");
        assertEquals("null",response.path("data").path("email").toString());
        assertEquals(response.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }
    @Test
    public void AddTrustedContactTest6() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "addTrustedContact", HttpMethod.POST, request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
    }
    @Test
    public void DeleteTrustedContactTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var req = TrustedContactReq.builder().email("mmge2009@yahoo.com").userId(node.path("data").path("id").asInt()).build();
        addAnotherUserAndEnabled();
        String json = ow.writeValueAsString(req);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "addTrustedContact", request, String.class);
        request = new HttpEntity<>(null,headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id="+node.path("data").path("id")+"&email=" + "mmge2009@hotmail.com",HttpMethod.DELETE,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Trusted Email not found\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }
    @Test
    public void DeleteTrustedContactTest2() throws Exception {
        JsonNode node = addUser();
        addAnotherUserAndEnabled();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null,headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id="+node.path("data").path("id")+"&email=" + "mmge2009@yahoo.com",HttpMethod.DELETE,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Email not in your Trusted Contacts\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
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
        request = new HttpEntity<>(null,headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "deleteTrustedContact?id="+node.path("data").path("id")+"&email=" + "mmge2009@yahoo.com",HttpMethod.DELETE,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Deleted Successfully\"");
        assertEquals("true",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }
    @Test
    public void DeleteTrustedContactTest4() throws Exception {
        JsonNode node = addUser();
        addAnotherUserAndEnabled();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null,headers);
        try {
            restTemplate.exchange(url + "deleteTrustedContact?id="+node.path("data").path("id")+"&email=" + "mmge2009@yahoo.com",HttpMethod.DELETE,request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
    }
    @Test
    public void GetPersonalTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getPersonalInfo?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(node.path("data").path("id").asInt(),res.path("data").path("id").asInt());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
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
            restTemplate.exchange(url + "getPersonalInfo?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
    }
    @Test
    public void CheckTokenTest1() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "checkTokenAvailability?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals("true",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
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
            restTemplate.exchange(url + "checkTokenAvailability?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
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
        request = new HttpEntity<>(null,headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getNumOfTrusted?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(1,res.path("data").asInt());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }
    @Test
    public void GetNumOfTrustedTest2() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        addAnotherUserAndEnabled();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null,headers);
        try {
            restTemplate.exchange(url + "getNumOfTrusted?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
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
        request = new HttpEntity<>(null,headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getAllTrusted?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"Executed Successfully\"");
        assertEquals("\"mmge2009@yahoo.com\"",res.path("data").get(0).path("email").toString());
        assertEquals(res.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void GetAllTrustedTest2() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        HttpEntity<String> request = new HttpEntity<>(null,headers);
        ResponseEntity<String> response = restTemplate.exchange(url + "getAllTrusted?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        JsonNode res = objectMapper.readTree(response.getBody());
        assertEquals(res.path("message").toString(), "\"User Don't have Trusted Contacts\"");
        assertEquals("null",res.path("data").toString());
        assertEquals(res.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }
    @Test
    public void GetAllTrustedTest3() throws Exception {
        JsonNode node = addUser();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        HttpEntity<String> request = new HttpEntity<>(null,headers);
        try {
            restTemplate.exchange(url + "getAllTrusted?id="+node.path("data").path("id"),HttpMethod.GET,request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
    }

    @Test
    public void SetVoiceTest1() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "setVoice?saved=0&id="+node.path("data").path("id"), request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(response.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(response.path("data").toString(), "false");
        assertEquals(response.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void SetVoiceTest2() throws Exception {
        JsonNode node = addUser();
        String token = node.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(null);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr = restTemplate.postForObject(url + "setVoice?saved=1&id="+node.path("data").path("id"), request, String.class);
        JsonNode response = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(response.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(response.path("data").toString(), "true");
        assertEquals(response.path("statusCode").toString(), "\"OK\"");
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
            restTemplate.exchange(url + "setVoice?saved=1?id="+node.path("data").path("id"),HttpMethod.POST,request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }
    }
}
