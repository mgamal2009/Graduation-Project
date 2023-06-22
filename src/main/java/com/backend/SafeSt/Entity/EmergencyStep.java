package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EmergencyStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "emergency_id")
    private Emergency emergency;

    public EmergencyStep(Emergency emergency) {
        this.emergency = emergency;
    }
}
