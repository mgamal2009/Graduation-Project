package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private double longitude;
    private double latitude;
    private String color;
    private float averageScore;

    @OneToMany(mappedBy = "location")
    private List<EmergencyInfo> emergencyInfos;
    @OneToMany(mappedBy = "location")
    private List<Report> reports;

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
