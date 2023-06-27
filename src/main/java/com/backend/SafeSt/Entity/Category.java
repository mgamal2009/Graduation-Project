/*
package com.backend.SafeSt.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "category")
    private Emergency emergency;
    @OneToMany(mappedBy = "category")
    private List<EmergencyInfo> emergencyInfos;

}
*/
