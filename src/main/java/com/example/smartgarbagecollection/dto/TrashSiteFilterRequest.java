package com.example.smartgarbagecollection.dto;

import java.util.List;
import java.util.UUID;

public class TrashSiteFilterRequest {
    private List<UUID> companyIds;


    public List<UUID> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<UUID> companyIds) {
        this.companyIds = companyIds;
    }
}
