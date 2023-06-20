package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String confirmationToken;

    private LocalDateTime createdDate;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    public ConfirmationToken(Customer customer){
        this.customer = customer;
        confirmationToken = UUID.randomUUID().toString();
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
        createdDate=LocalDateTime.now();
    }
}
