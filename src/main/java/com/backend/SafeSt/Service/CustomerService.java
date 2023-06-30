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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final TrustedContactRepository trustedContactRepository;
    private final CustomerMapper customerMapper;
    private final TrustedContactMapper trustedContactMapper;

    @Transactional
    public TrustedContactModel addTrustedContact(TrustedContactReq req, Authentication auth) throws Exception {
        Customer c = checkLoggedIn(req.getUserId(), auth);
        if (!(Validation.validateString(req.getEmail()))) {
            throw new Exception("Email couldn't be empty");
        }
        Optional<Customer> trusted = customerRepository.findByEmailAndEnabled(req.getEmail(),true);
        if (trusted.isEmpty()) {
            throw new Exception("Email not found");
        }
        Customer foundTrusted = trusted.get();
        if (Objects.equals(foundTrusted.getId(), c.getId())){
            throw new Exception("You can't add your self");
        }
        Optional<TrustedContact> found = trustedContactRepository.findByCustomer_IdAndTrusted_Id(c.getId(), foundTrusted.getId());
        if (found.isPresent()){
            throw new Exception("Already In Your List");
        }
        var trustedContact = TrustedContact.builder()
                .customer(c)
                .trusted(foundTrusted)
                .build();
        trustedContactRepository.save(trustedContact);
        return trustedContactMapper.convertEntityToModel(trustedContact);
    }

    public static Customer checkLoggedIn(int id, Authentication auth) throws Exception {
        Customer c = (Customer) auth.getPrincipal();
        if (!Objects.equals(c.getId(), id)) {
            throw new Exception("Authentication Error");
        }
        return c;
    }

    @Transactional
    public boolean deleteTrustedContact(int id, String email, Authentication auth) throws Exception {
        Customer customer = checkLoggedIn(id, auth);
        Optional<Customer> trusted = customerRepository.findByEmail(email);
        if (trusted.isEmpty()) {
            throw new Exception("Trusted Email not found");
        }
        Customer foundTrusted = trusted.get();
        Optional<TrustedContact> found = trustedContactRepository.findByCustomer_IdAndTrusted_Id(id, foundTrusted.getId());
        if (found.isEmpty()) {
            throw new Exception("Email not in your Trusted Contacts");
        }
        trustedContactRepository.deleteTrustedContactByCustomer_IdAndTrusted_Id(customer.getId(), foundTrusted.getId());
        return true;
    }
    public CustomerModel updatePersonalInfo(CustomerReq req,Authentication auth) throws Exception {
        Customer customer =  checkLoggedIn(req.getId(), auth);
        if (!req.getFirstname().isBlank()){
            customer.setFirstName(req.getFirstname());
        }if (!req.getLastname().isBlank()){
            customer.setLastName(req.getLastname());
        }
        /*if (req.getPassword() != null && req.getConfirmationPassword() != null && req.getOldPassword() != null ){
            if (!req.getPassword().isBlank()){
                if (!req.getConfirmationPassword().isBlank()&& !req.getOldPassword().isBlank()){
                    if (req.getPassword().equals(req.getConfirmationPassword())){
                        customer.setPassword(passwordEncoder.encode(req.getPassword()));
                    }else{
                        throw new Exception("Password and Confirmation Password isn't the same");
                    }
                }else{
                    throw new Exception("Old Password and Confirmation Password can't be empty");
                }
            }
        }
        if (req.getPassword() != null || req.getConfirmationPassword() != null || req.getOldPassword() != null ) {
            throw new Exception("Passwords can't be empty");
        }*/
        if (!req.getPhoneNumber().isBlank()){
            customer.setPhoneNumber(req.getPhoneNumber());
        }
        customerRepository.save(customer);
        return customerMapper.convertEntityToModel(customer);
    }

    public CustomerModel getPersonalInfo(int id, Authentication auth) throws Exception {
        Customer customer =  checkLoggedIn(id, auth);
        return customerMapper.convertEntityToModel(customer);
    }
    /*public CustomerModel getTrustedInfo(int id,String email, Authentication auth) throws Exception {
        checkLoggedIn(id, auth);
        Optional<Customer> trusted = customerRepository.findByEmail(email);
        if (trusted.isEmpty()) {
            throw new Exception("Trusted Email not found");
        }
        Customer foundTrusted = trusted.get();
        Optional<TrustedContact> found = trustedContactRepository.findByCustomer_IdAndTrusted_Id(id, foundTrusted.getId());
        if (found.isEmpty()) {
            throw new Exception("Email not in your Trusted Contacts");
        }
        return CustomerModel.builder()
                .firstname(foundTrusted.getFirstName())
                .lastname(foundTrusted.getLastName())
                .id(foundTrusted.getId())
                .email(foundTrusted.getEmail())
                .phoneNumber(foundTrusted.getPhoneNumber())
                .build();
    }*/
    public int getNumOfTrusted(int id, Authentication auth) throws Exception {
        Customer c = checkLoggedIn(id, auth);
        ArrayList<TrustedContact> list = trustedContactRepository.findAllByCustomer_Id(c.getId());
        return list.size();
    }

    public ArrayList<TrustedContactModel> getAllTrustedContacts(int id, Authentication auth) throws Exception {
        Customer c = checkLoggedIn(id, auth);
        ArrayList<TrustedContact> list = trustedContactRepository.findAllByCustomer_Id(c.getId());
        if (list.isEmpty()){
            throw new Exception("User Don't have Trusted Contacts");
        }
        ArrayList<TrustedContactModel> listModel = new ArrayList<>();
        for (TrustedContact t : list){
            listModel.add(trustedContactMapper.convertEntityToModel(t));
        }
        return listModel;
    }
}
