package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "emergency")
    private List<EmergencyInfo> emergencyInfos;
    @OneToMany(mappedBy = "emergency")
    private List<EmergencyStep> emergencySteps;


    public Emergency(Category category) {
        this.category = category;
    }

}
