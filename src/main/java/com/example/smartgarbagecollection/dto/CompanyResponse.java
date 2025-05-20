package com.example.smartgarbagecollection.dto;


import lombok.Data;
import java.util.UUID;

@Data
public class CompanyResponse {
    private UUID id;
    private String name;
    private String inn;
    private String website;
    private String description;
}
