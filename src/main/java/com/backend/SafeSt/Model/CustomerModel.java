package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CustomerModel {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private boolean savedVoice;
}
