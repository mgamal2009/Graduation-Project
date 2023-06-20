package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.CustomerLocation;
import com.backend.SafeSt.Mapper.CustomerLocationMapper;
import com.backend.SafeSt.Model.CustomerLocationModel;
import com.backend.SafeSt.Repository.CustomerLocationRepository;
import com.backend.SafeSt.Repository.CustomerRepository;
import com.backend.SafeSt.Request.CustomerLocationReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerLocationService {

    private final CustomerLocationRepository customerLocationRepository;
    private final CustomerLocationMapper customerLocationMapper;

    @Transactional
    public CustomerLocationModel updateCustomerLocation(CustomerLocationReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        if (!(Validation.validateDouble(req.getLatitude(), req.getLongitude()))) {
            throw new Exception("Longitude and Latitude Should be Double");
        }
        if (!(Validation.validateInt(req.getCustomerId()))) {
            throw new Exception("Customer Id Should be Integer");
        }
        CustomerLocation location;
        Optional<CustomerLocation> l = customerLocationRepository.findByCustomer_Id(req.getCustomerId());
        if (l.isPresent()) {
            location = l.get();
            location.setLongitude(req.getLongitude());
            location.setLatitude(req.getLatitude());
        } else {
            location = CustomerLocation.builder()
                    .customer(c)
                    .longitude(req.getLongitude())
                    .latitude(req.getLatitude())
                    .build();
        }
        customerLocationRepository.save(location);
        return customerLocationMapper.convertEntityToModel(location);
    }
}
