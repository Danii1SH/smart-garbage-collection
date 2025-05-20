package com.example.smartgarbagecollection.service;

import com.example.smartgarbagecollection.dto.TrashSiteRequest;
import com.example.smartgarbagecollection.dto.TrashSiteResponse;

import java.util.List;
import java.util.UUID;

public interface TrashSiteService {
    TrashSiteResponse create(TrashSiteRequest request);
    List<TrashSiteResponse> getAll();
    List<TrashSiteResponse> getAllByCompany(UUID companyId);
    TrashSiteResponse getById(UUID id);
    TrashSiteResponse update(UUID id, TrashSiteRequest request);
    void delete(UUID id);
}