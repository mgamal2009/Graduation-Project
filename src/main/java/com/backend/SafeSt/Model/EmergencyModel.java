package com.backend.SafeSt.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmergencyModel {
    private Integer id;
    private Integer categoryId;
}
