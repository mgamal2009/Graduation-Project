package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private double longitude;
    private double latitude;

    @OneToOne()
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
