package com.backend.SafeSt.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReq {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
//    private String oldPassword;
    private String confirmationPassword;
    private String phoneNumber;
}
