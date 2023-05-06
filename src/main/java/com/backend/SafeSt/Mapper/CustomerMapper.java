package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Model.CustomerModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class CustomerMapper {
    private final CustomerLocationMapper customerLocationMapper;

    public CustomerModel convertEntityToModel(Customer customer) {
        var customerModel = CustomerModel.builder()
                .id(customer.getId())
                .firstname(customer.getFirstName())
                .lastname(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
        if (customer.getCustomerLocation() != null) {
            customerModel.setCustomerLocation(customerLocationMapper.convertEntityToModel(customer.getCustomerLocation()));
        }
        return customerModel;
    }
}
