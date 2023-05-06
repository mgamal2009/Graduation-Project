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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerLocationService {

    private final CustomerLocationRepository customerLocationRepository;
    private final CustomerRepository customerRepository;
    private final CustomerLocationMapper customerLocationMapper;

    @Transactional
    public CustomerLocationModel updateCustomerLocation(CustomerLocationReq req) throws Exception {
        if (!(Validation.validateDouble(req.getLatitude(), req.getLongitude()))) {
            throw new Exception("Longitude and Latitude Should be Double");
        }
        if (!(Validation.validateLong(req.getCustomerId()))) {
            throw new Exception("Customer Id Should be Long");
        }
        CustomerLocation location;
        Optional<CustomerLocation> l = customerLocationRepository.findByCustomer_Id(req.getCustomerId());
        if (l.isPresent()) {
            location = l.get();
            location.setLongitude(req.getLongitude());
            location.setLatitude(req.getLatitude());
        } else {
            Optional<Customer> c = customerRepository.findById(req.getCustomerId());
            if (c.isEmpty()) {
                throw new Exception("Customer not Found");
            }
            location = CustomerLocation.builder()
                    .customer(c.get())
                    .longitude(req.getLongitude())
                    .latitude(req.getLatitude())
                    .build();
        }
        customerLocationRepository.save(location);
        return customerLocationMapper.convertEntityToModel(location);
    }
}
