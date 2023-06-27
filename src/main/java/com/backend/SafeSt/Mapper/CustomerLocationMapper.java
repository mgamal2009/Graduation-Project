/*
package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.CustomerLocation;
import com.backend.SafeSt.Model.CustomerLocationModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CustomerLocationMapper {
    public CustomerLocationModel convertEntityToModel(CustomerLocation customerLocation){
        return CustomerLocationModel.builder()
                .id(customerLocation.getId())
                .longitude(customerLocation.getLongitude())
                .latitude(customerLocation.getLatitude())
                .customerId(customerLocation.getCustomer().getId())
                .build();
    }
}
*/
