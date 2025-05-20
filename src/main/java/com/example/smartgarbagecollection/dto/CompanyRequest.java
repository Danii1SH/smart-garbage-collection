package com.example.smartgarbagecollection.dto;

import lombok.Data;

@Data
public class CompanyRequest {
    private String name;
    private String inn;
    private String website;
    private String description;
}
