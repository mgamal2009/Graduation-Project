package com.backend.SafeSt.Service;

import com.backend.SafeSt.Config.JwtService;
import com.backend.SafeSt.Entity.CustomerLocation;
import com.backend.SafeSt.Entity.Token;
import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Enum.Role;
import com.backend.SafeSt.Enum.TokenType;
import com.backend.SafeSt.Repository.CustomerLocationRepository;
import com.backend.SafeSt.Repository.TokenRepository;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Response.AuthenticationResponse;
import com.backend.SafeSt.Util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final CustomerLocationRepository customerLocationRepository;


    public AuthenticationResponse register(CustomerReq req) throws Exception {
        if (!(Validation.validateString(req.getFirstname(), req.getLastname(), req.getEmail(), req.getPhoneNumber(),req.getPassword()))){
            throw new Exception("Name, Email, Phone Number, and Password couldn't be empty");
        }
        if(customerRepository.findByEmail(req.getEmail()).isPresent()){
            throw  new Exception("This Email is already used");
        }
        var customer = Customer.builder()
                .firstName(req.getFirstname())
                .lastName(req.getLastname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phoneNumber(req.getPhoneNumber())
                .role(Role.Customer)
                .build();
        var savedCustomer = customerRepository.save(customer);
        var location = CustomerLocation.builder()
                .customer(savedCustomer)
                .build();
        var savedLocation = customerLocationRepository.save(location);
        savedCustomer.setCustomerLocation(savedLocation);
        savedCustomer = customerRepository.save(customer);
        var jwtToken = jwtService.generateToken(customer);
        saveCustomerToken(savedCustomer, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new Exception("User not found"));
        var jwtToken = jwtService.generateToken(customer);
        revokeAllCustomerTokens(customer);
        saveCustomerToken(customer, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    private void revokeAllCustomerTokens(Customer customer){
        var validCustomerTokens = tokenRepository.findAllValidTokensByCustomer(customer.getId());
        if (validCustomerTokens.isEmpty())
            return;
        validCustomerTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validCustomerTokens);
    }

    private void saveCustomerToken(Customer customer, String jwtToken) {
        var token = Token.builder()
                .customer(customer)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
