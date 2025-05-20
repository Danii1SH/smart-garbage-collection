package com.example.smartgarbagecollection.service.impl;

import com.example.smartgarbagecollection.dto.CompanyResponse;
import com.example.smartgarbagecollection.dto.TrashSiteRequest;
import com.example.smartgarbagecollection.dto.TrashSiteResponse;
import com.example.smartgarbagecollection.model.Company;
import com.example.smartgarbagecollection.model.TrashSite;
import com.example.smartgarbagecollection.repository.CompanyRepository;
import com.example.smartgarbagecollection.repository.TrashSiteRepository;
import com.example.smartgarbagecollection.service.TrashSiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrashSiteServiceImpl implements TrashSiteService {

    private final TrashSiteRepository repository;
    private final CompanyRepository companyRepository;

    @Override
    public TrashSiteResponse create(TrashSiteRequest request) {
        log.info("Create TrashSite: {}", request);
        TrashSite site = new TrashSite();
        mapToEntity(request, site);
        return mapToResponse(repository.save(site));
    }

    @Override
    public List<TrashSiteResponse> getAll() {
        List<TrashSite> trashSites = repository.findAll();
        return trashSites.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrashSiteResponse> getAllByCompany(UUID companyId) {
        List<TrashSite> trashSites = repository.findAllByCompanyId(companyId);
        return trashSites.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TrashSiteResponse getById(UUID id) {
        TrashSite site = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trash site not found"));
        return mapToResponse(site);
    }

    @Override
    public TrashSiteResponse update(UUID id, TrashSiteRequest request) {
        TrashSite site = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trash site not found"));
        mapToEntity(request, site);
        return mapToResponse(repository.save(site));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private TrashSiteResponse mapToResponse(TrashSite site) {
        TrashSiteResponse res = new TrashSiteResponse();
        res.setId(site.getId());
        res.setSiteNumber(site.getSiteNumber());
        res.setRegion(site.getRegion());
        res.setCity(site.getCity());
        res.setDistrict(site.getDistrict());
        res.setRouteNumber(site.getRouteNumber());
        res.setAddress(site.getAddress());
        res.setContainerCount(site.getContainerCount());
        res.setStatus(site.getStatus());
        res.setLatitude(site.getLatitude());
        res.setLongitude(site.getLongitude());

        Company company = site.getCompany();
        if (company != null) {
            CompanyResponse dto = new CompanyResponse();
            dto.setId(company.getId());
            dto.setName(company.getName());
            res.setCompany(dto);
        }
        return res;
    }

    private void mapToEntity(TrashSiteRequest req, TrashSite site) {
        site.setSiteNumber(req.getSiteNumber());
        site.setRegion(req.getRegion());
        site.setCity(req.getCity());
        site.setDistrict(req.getDistrict());
        site.setRouteNumber(req.getRouteNumber());
        site.setAddress(req.getAddress());
        site.setContainerCount(req.getContainerCount());
        site.setStatus(req.getStatus());
        site.setLatitude(req.getLatitude());
        site.setLongitude(req.getLongitude());

        if (req.getCompanyId() != null) {
            Company company = companyRepository.findById(req.getCompanyId())
                    .orElseThrow(() -> new NoSuchElementException("Company not found"));
            site.setCompany(company);
        }
    }
}
