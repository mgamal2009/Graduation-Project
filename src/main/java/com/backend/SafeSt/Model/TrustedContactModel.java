package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustedContactModel {
    private boolean addedOrDeleted;
    private String email;
}
