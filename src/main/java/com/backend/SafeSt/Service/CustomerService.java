package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.TrustedContact;
import com.backend.SafeSt.Mapper.CustomerMapper;
import com.backend.SafeSt.Mapper.TrustedContactMapper;
import com.backend.SafeSt.Model.CustomerModel;
import com.backend.SafeSt.Model.TrustedContactModel;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Repository.TrustedContactRepository;
import com.backend.SafeSt.Request.CustomerReq;
import com.backend.SafeSt.Request.TrustedContactReq;
import com.backend.SafeSt.Util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final TrustedContactRepository trustedContactRepository;

    private final CustomerMapper customerMapper;
    private final TrustedContactMapper trustedContactMapper;
    private final PasswordEncoder passwordEncoder;

    public TrustedContactModel addTrustedContact(TrustedContactReq req, Authentication auth) throws Exception {
        Customer c = checkLoggedIn(req, auth);
        Optional<Customer> trusted = customerRepository.findByEmail(req.getEmail());
        if (trusted.isEmpty()) {
            throw new Exception("Email not found");
        }
        Customer foundTrusted = trusted.get();
        var trustedContact = TrustedContact.builder()
                .customer(c)
                .trusted(foundTrusted)
                .build();
        trustedContactRepository.save(trustedContact);
        return trustedContactMapper.convertEntityToModel(trustedContact);
    }

    private static Customer checkLoggedIn(TrustedContactReq req, Authentication auth) throws Exception {
        Customer c = (Customer) auth.getPrincipal();
        if (!Objects.equals(c.getId(), req.getUserId())) {
            throw new Exception("Authentication Error");
        }
        if (!(Validation.validateString(req.getEmail()))) {
            throw new Exception("Email couldn't be empty");
        }
        return c;
    }

    public boolean deleteTrustedContact(TrustedContactReq req, Authentication auth) throws Exception {
        checkLoggedIn(req, auth);
        Optional<Customer> trusted = customerRepository.findByEmail(req.getEmail());
        if (trusted.isEmpty()) {
            throw new Exception("Email not found");
        }
        Customer foundTrusted = trusted.get();
        trustedContactRepository.deleteByTrusted_Id(foundTrusted.getId());
        return true;
    }
    public CustomerModel updatePersonalInfo(CustomerReq req) throws Exception {
        Optional<Customer> c = customerRepository.findByEmail(req.getEmail());
        if (c.isEmpty()) {
            throw new Exception("Customer Not Found !!");
        }
        Customer customer = c.get();
        if (!req.getFirstname().isBlank()){
            customer.setFirstName(req.getFirstname());
        }if (!req.getLastname().isBlank()){
            customer.setLastName(req.getLastname());
        }if (!req.getFirstname().isBlank()){
            customer.setFirstName(req.getFirstname());
        }if (!req.getPassword().isBlank()){
            if (!req.getConfirmationPassword().isBlank()&& !req.getOldPassword().isBlank()){
                if (req.getPassword().equals(req.getConfirmationPassword())){
                    customer.setPassword(passwordEncoder.encode(req.getPassword()));
                }else{
                    throw new Exception("Password and Confirmation Password isn't the same");
                }
            }else{
                throw new Exception("Old Password and Confirmation Password can't be empty");
            }
        }if (!req.getPhoneNumber().isBlank()){
            customer.setPhoneNumber(req.getPhoneNumber());
        }
        customerRepository.save(customer);
        return customerMapper.convertEntityToModel(customer);
    }


}
