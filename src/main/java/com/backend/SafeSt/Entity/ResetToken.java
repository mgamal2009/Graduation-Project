package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    private String resetToken;

    private LocalDateTime createdDate;
    private boolean used;

    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    private Customer customer;


    public ResetToken(Customer customer) {
        this.customer = customer;
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
        createdDate = LocalDateTime.now();
        resetToken = UUID.randomUUID().toString();
        used=false;
    }

}
