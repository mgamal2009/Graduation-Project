package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Model.CustomerModel;
import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class CustomerMapper {

    public CustomerModel convertEntityToModel(Customer customer) {
        return CustomerModel.builder()
                .id(customer.getId())
                .firstname(customer.getFirstName())
                .lastname(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .savedVoice(customer.isSavedVoice())
                .build();
    }
}
