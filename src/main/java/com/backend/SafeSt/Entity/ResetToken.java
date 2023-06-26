package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean used;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
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
