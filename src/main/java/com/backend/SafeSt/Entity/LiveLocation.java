package com.backend.SafeSt.Entity;


import jakarta.persistence.Entity;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
public class LiveLocation extends EmergencyStep {

    public LiveLocation(Emergency emergency) {
        super(emergency);
    }
}