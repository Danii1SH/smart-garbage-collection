package com.example.smartgarbagecollection.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TrashSiteResponse {
    private UUID id;
    private String siteNumber;
    private String region;
    private String city;
    private String district;
    private String routeNumber;
    private String address;
    private CompanyResponse company;
    private int containerCount;
    private String status;
    private Double latitude;
    private Double longitude;
}

