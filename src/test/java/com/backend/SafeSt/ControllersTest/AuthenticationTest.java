package com.backend.SafeSt.ControllersTest;

import com.backend.SafeSt.Entity.ConfirmationToken;
import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Repository.ConfirmationTokenRepository;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Util.RSAUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTest {

    @LocalServerPort
    int randomServerPort;

    private RestTemplate restTemplate;
    private String url;
    JSONObject personJsonObject;
    HttpHeaders headers;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" + randomServerPort + "/auth/";
        personJsonObject = new JSONObject();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        customerRepository.deleteAll();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void RegisterTest1() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "register", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals("\"Created Successfully\"", root.path("message").toString());
        assertEquals("true", root.path("data").toString());
        assertEquals("\"OK\"", root.path("statusCode").toString());
    }

    @Test
    public void RegisterTest2() throws JsonProcessingException {
        var req = CustomerReq.builder().firstname("").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "register", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Fields couldn't be empty\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void RegisterTest3() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "register", request, String.class);
        personResultAsJsonStr =
                restTemplate.postForObject(url + "register", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"This Email is already used\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void RegisterTest4() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("123").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "register", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Password and Confirmation Password Should be the Same!!\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void LoginTest1() throws Exception {
        var req = AuthenticationRequest.builder().email("mahmoud.mohamedgamal1@gmail.com").password("1234").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Bad credentials\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void LoginTest2() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        /*Customer c = customerRepository.findByEmail("mahmoud.mohamedgamal1@gmail.com").get();
        c.setEnabled(true);
        customerRepository.save(c);*/
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"User is disabled\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void LoginTest3() throws Exception {
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email("mahmoud.mohamedgamal1@gmail.com").password("1234").confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        Customer c = customerRepository.findByEmail("mahmoud.mohamedgamal1@gmail.com").get();
        c.setEnabled(true);
        customerRepository.save(c);
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Executed Successfully\"");
        assertNotNull(root.path("data").path("id"));
        assertNotNull(root.path("data").path("token"));
        assertNotNull(root.path("data").path("saved"), "0");
        assertEquals(root.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void ConfirmAccount1() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        String token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId()).getConfirmationToken();
        token = token.concat("d5");
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token));
        String personResultAsJsonStr = restTemplate.getForObject(url + "confirm-account?urlToken=" + encryptedToken, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Invalid Link!\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void ConfirmAccount2() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        String token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId()).getConfirmationToken();
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token));
        Customer c = customer.get();
        c.setEnabled(true);
        customerRepository.save(c);
        String personResultAsJsonStr = restTemplate.getForObject(url + "confirm-account?urlToken=" + encryptedToken, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Your Account is  Already Confirmed!\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void ConfirmAccount3() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        ConfirmationToken token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId());
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token.getConfirmationToken()));
        token.setCreatedDate(token.getCreatedDate().minusMinutes(65));
        confirmationTokenRepository.save(token);
        String personResultAsJsonStr = restTemplate.getForObject(url + "confirm-account?urlToken=" + encryptedToken, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Link is Expired! New Link Was Sent to Your Email.\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }

    @Test
    public void ConfirmAccount4() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        ConfirmationToken token = confirmationTokenRepository.findByCustomer_Id(customer.get().getId());
        String encryptedToken = Base64.getEncoder().encodeToString(RSAUtil.encrypt(token.getConfirmationToken()));
        String personResultAsJsonStr = restTemplate.getForObject(url + "confirm-account?urlToken=" + encryptedToken, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Your Account is Confirmed Successfully!\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void LogoutTest1() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(customer.get().getId());
        customerRepository.save(customer.get());
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        String token = root.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        personResultAsJsonStr = restTemplate.postForObject(url + "logout", request, String.class);
        root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Executed Successfully\"");
        assertEquals(root.path("data").toString(), "true");
        assertEquals(root.path("statusCode").toString(), "\"OK\"");
    }

    @Test
    public void LogoutTest2() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(customer.get().getId());
        customerRepository.save(customer.get());
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        String token = root.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1));
        req.setId(1010);
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        personResultAsJsonStr = restTemplate.postForObject(url + "logout", request, String.class);
        root = objectMapper.readTree(personResultAsJsonStr);
        assertEquals(root.path("message").toString(), "\"Authentication Error\"");
        assertEquals(root.path("data").toString(), "null");
        assertEquals(root.path("statusCode").toString(), "\"INTERNAL_SERVER_ERROR\"");
    }
    @Test
    public void LogoutTest3() throws Exception {
        String username = "mahmoud.mohamedgamal1@gmail.com";
        String password = "1234";
        var req = CustomerReq.builder().firstname("mahmoud").lastname("gamal").email(username).password(password).confirmationPassword("1234").phoneNumber("01012").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(req);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url + "register", request, String.class);
        var customer = customerRepository.findByEmail(username);
        customer.get().setEnabled(true);
        req.setId(customer.get().getId());
        customerRepository.save(customer.get());
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        String personResultAsJsonStr =
                restTemplate.postForObject(url + "authenticate", request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        String token = root.path("data").path("token").toString();
        headers.setBearerAuth(token.substring(1, token.length() - 1) + "de");
        req.setId(customer.get().getId());
        json = ow.writeValueAsString(req);
        request = new HttpEntity<>(json, headers);
        try {
            restTemplate.exchange(url + "logout", HttpMethod.POST, request, String.class);
        }catch (HttpClientErrorException e){
            assertEquals("403 FORBIDDEN",e.getStatusCode().toString());
        }

    }
}