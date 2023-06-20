package com.backend.SafeSt.Service;

import com.backend.SafeSt.Config.JwtService;
import com.backend.SafeSt.Entity.*;
import com.backend.SafeSt.Enum.Role;
import com.backend.SafeSt.Enum.TokenType;
import com.backend.SafeSt.Mapper.CustomerMapper;
import com.backend.SafeSt.Model.CustomerModel;
import com.backend.SafeSt.Repository.*;
import com.backend.SafeSt.Request.AuthenticationRequest;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.ResetPasswordReq;
import com.backend.SafeSt.Response.AuthenticationResponse;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Util.RSAUtil;
import com.backend.SafeSt.Util.ResponseMessage;
import com.backend.SafeSt.Util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final CustomerLocationRepository customerLocationRepository;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final CustomerMapper customerMapper;
    private final ResetTokenRepository resetTokenRepository;

    @Transactional
    public CustomerModel register(CustomerReq req) throws Exception {
        if (!(Validation.validateString(req.getFirstname(), req.getLastname(), req.getEmail(), req.getPhoneNumber(), req.getPassword()))) {
            throw new Exception("Name, Email, Phone Number, and Password couldn't be empty");
        }
        if (customerRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new Exception("This Email is already used");
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
        /*var jwtToken = jwtService.generateToken(customer);
        saveCustomerToken(savedCustomer, jwtToken);*/
        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);
        emailService.sendEmail(customer.getFirstName(), customer.getEmail(), confirmationToken.getConfirmationToken());
        return customerMapper.convertEntityToModel(savedCustomer);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("User not found"));
        if(!customer.isEnabled()){
            throw new Exception("Please Activate Your Account First");
        }
        var jwtToken = jwtService.generateToken(customer);
        deleteCustomerTokens(customer);
        saveCustomerToken(customer, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
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
            return new MainResponse(HttpStatus.OK, "Already Confirmed!");
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
        if (token.getCreatedDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new Exception("Link is Expired!");
        }
        customer.setEnabled(true);
        customerRepository.save(customer);
        return new MainResponse(HttpStatus.OK, "Confirmed Successfully!");
    }
    public MainResponse resendConfirmationEmail(String oldToken) throws Exception {
        oldToken = oldToken.replace(" ", "+");
        String decryptedToken = RSAUtil.decrypt(oldToken);
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(decryptedToken);
        if (token == null) {
            throw new Exception("Invalid Link!");
        }
        Customer customer = token.getCustomer();
        confirmationTokenRepository.delete(token);
        ConfirmationToken newConfirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(newConfirmationToken);
        emailService.sendEmail(customer.getFirstName(), customer.getEmail(), newConfirmationToken.getConfirmationToken());
        return new MainResponse(HttpStatus.OK, "A New Confirmation Link has been sent to You!");
    }

    public MainResponse sendResetPasswordMail(String email) {
        try {
            var customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User not found"));
            ResetToken resetToken = new ResetToken(customer);
            resetTokenRepository.save(resetToken);
            emailService.sendResetMail(customer.getEmail(), resetToken.getResetToken());
            return new MainResponse(HttpStatus.OK, ResponseMessage.EXECUTED);
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    //---> update password
    public String updatePassword(ResetPasswordReq req) throws Exception {
        String decryptedToken = RSAUtil.decrypt(req.getUrlToken().replace(" ", "+"));
        var resetToken = resetTokenRepository.findByResetToken(decryptedToken)
                .orElseThrow(() -> new Exception("Reset Token Not Found!!"));
        if (resetToken.isUsed())
            throw new Exception("Reset Token Is Already Used!!");
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
        if (resetToken.getCreatedDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new Exception("Reset Token Is Expired!!");
        }
        var c = customerRepository.findById(resetToken.getCustomer().getId())
                .orElseThrow(() -> new Exception("User not found"));
        c.setPassword(passwordEncoder.encode(req.getNewPassword()));
        customerRepository.save(c);
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
        return "Password Reset Successfully";
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
}
