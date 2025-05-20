package com.example.smartgarbagecollection.service;

import com.example.smartgarbagecollection.dto.CompanyRequest;
import com.example.smartgarbagecollection.dto.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request);
    List<CompanyResponse> getAll();
    CompanyResponse getById(UUID id);
    CompanyResponse update(UUID id, CompanyRequest request);
    void delete(UUID id);
}
