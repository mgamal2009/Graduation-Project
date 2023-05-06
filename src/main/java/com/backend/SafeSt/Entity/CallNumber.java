package com.backend.SafeSt.Entity;


import jakarta.persistence.Entity;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
public class CallNumber extends EmergencyStep {

    private String phoneNumber;


    public CallNumber(Emergency emergency, String phoneNumber) {
        super(emergency);
        this.phoneNumber = phoneNumber;
    }
}