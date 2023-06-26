package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustedContactModel {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
}
