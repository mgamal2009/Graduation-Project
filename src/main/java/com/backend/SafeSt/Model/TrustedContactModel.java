package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustedContactModel {
    private String email;
    private String name;
}
