package com.example.smartgarbagecollection.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TrashSiteRequest {
    private String siteNumber;
    private String region;
    private String city;
    private String district;
    private String address;
    private UUID companyId;
    private int containerCount;
    private String status;
    private Double latitude;
    private Double longitude;
}

