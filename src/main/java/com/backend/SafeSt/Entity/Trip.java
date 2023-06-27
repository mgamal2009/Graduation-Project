package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private Timestamp startedAt;
    private Timestamp estimatedEnd;
    private int estimatedTime;
    private long remainingTime;
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean ended;
    @Column(precision = 3)
    private double sourceLongitude;
    @Column(precision = 3)
    private double sourceLatitude;
    @Column(precision = 3)
    private double destinationLongitude;
    @Column(precision = 3)
    private double destinationLatitude;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private Customer customer;


}
