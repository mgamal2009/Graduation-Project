package com.backend.SafeSt.Service;

import com.backend.SafeSt.Config.JwtService;
import com.backend.SafeSt.Entity.ConfirmationToken;
import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Token;
import com.backend.SafeSt.Enum.Role;
import com.backend.SafeSt.Enum.TokenType;
import com.backend.SafeSt.Repository.ConfirmationTokenRepository;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.TokenRepository;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Response.AuthenticationResponse;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Util.RSAUtil;
import com.backend.SafeSt.Util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public boolean register(CustomerReq req) throws Exception {
        if (!(Validation.validateString(req.getFirstname(), req.getLastname(), req.getEmail(), req.getPhoneNumber(), req.getPassword(), req.getConfirmationPassword()))) {
            throw new Exception("Fields couldn't be empty");
        }
        if (customerRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new Exception("This Email is already used");
        }
        if (!req.getPassword().equals(req.getConfirmationPassword())) {
            throw new Exception("Password and Confirmation Password Should be the Same!!");
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
        ConfirmationToken confirmationToken = new ConfirmationToken(savedCustomer);
        confirmationTokenRepository.save(confirmationToken);
        emailService.sendEmail(savedCustomer.getFirstName(), savedCustomer.getEmail(), confirmationToken.getConfirmationToken());
        return true;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var customer = (Customer) auth.getPrincipal();
        var jwtToken = jwtService.generateToken(customer);
        deleteCustomerTokens(customer);
        saveCustomerToken(customer, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(customer.getId())
                .savedVoice(customer.isSavedVoice())
                .build();
    }

    public MainResponse confirmMail(String confirmationToken) throws Exception {
        confirmationToken = confirmationToken.replace(" ", "+");
        String decryptedToken = RSAUtil.decrypt(confirmationToken);
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(decryptedToken);
        if (token == null)
            throw new Exception("Invalid Link!");
        var customer = customerRepository.findByEmail(token.getCustomer().getEmail())
                .orElseThrow(() -> new Exception("User not found"));
        if (customer.isEnabled())
            return new MainResponse(HttpStatus.OK, "Your Account is  Already Confirmed!");

        if (token.getCreatedDate().plusHours(1).isBefore(ZonedDateTime.now(ZoneId.of("Africa/Cairo")).toLocalDateTime())) {
            resendConfirmationEmail(token);
            throw new Exception("Link is Expired! New Link Was Sent to Your Email.");
        }
        customer.setEnabled(true);
        customerRepository.save(customer);
        return new MainResponse(HttpStatus.OK, "Your Account is Confirmed Successfully!");
    }

    public void resendConfirmationEmail(ConfirmationToken token) throws Exception {
        Customer customer = token.getCustomer();
        confirmationTokenRepository.delete(token);
        ConfirmationToken newConfirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(newConfirmationToken);
        emailService.sendEmail(customer.getFirstName(), customer.getEmail(), newConfirmationToken.getConfirmationToken());
    }

    private void deleteCustomerTokens(Customer customer) {
        var validCustomerTokens = tokenRepository.findAllTokensByCustomer(customer);
        if (validCustomerTokens.isEmpty())
            return;
        tokenRepository.deleteAll(validCustomerTokens);
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

    public boolean logout(CustomerReq request, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(request.getId(), auth);
        var token = tokenRepository.findByCustomer_Id(request.getId())
                .orElseThrow(() -> new Exception("Token not Found"));
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
        return true;
    }
}
