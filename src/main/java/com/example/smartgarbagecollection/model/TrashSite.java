package com.example.smartgarbagecollection.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "trash_sites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrashSite {

    @Id
    @GeneratedValue
    private UUID id;

    private String siteNumber;
    private String region;
    private String city;
    private String district;
    private String address;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private int containerCount;
    private String status;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;
}
