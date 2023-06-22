package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private Timestamp estimatedTime;
    private Timestamp totalTime;

    private boolean ended;

    private double sourceLongitude;
    private double sourceLatitude;
    private double destinationLongitude;
    private double destinationLatitude;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "customer_id")
    private Customer customer;


}
