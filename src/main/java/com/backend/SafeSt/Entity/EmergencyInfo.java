package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmergencyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private Timestamp date;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "emergency_id")
    private Emergency emergency;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "location_id")
    private Location location;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "report_id")
    private Report report;

}
