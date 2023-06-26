package com.backend.SafeSt.Request;

import com.backend.SafeSt.Model.CustomerLocationModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
