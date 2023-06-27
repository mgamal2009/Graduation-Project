package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;


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
    @Column(precision = 3)
    private double longitude;
    @Column(precision = 3)
    private double latitude;
//    private String color;
    private double averageScore;
    private long reportsCount = 0;

    /*@OneToMany(mappedBy = "location")
    private List<EmergencyInfo> emergencyInfos;
    @OneToMany(mappedBy = "location")
    private List<Report> reports;*/

}
