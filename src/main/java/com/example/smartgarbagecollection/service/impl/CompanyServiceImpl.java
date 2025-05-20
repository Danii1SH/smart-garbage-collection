package com.example.smartgarbagecollection.service.impl;

import com.example.smartgarbagecollection.audit.Audit;
import com.example.smartgarbagecollection.dto.CompanyRequest;
import com.example.smartgarbagecollection.dto.CompanyResponse;
import com.example.smartgarbagecollection.model.Company;
import com.example.smartgarbagecollection.repository.CompanyRepository;
import com.example.smartgarbagecollection.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;

    @Audit(action = "CREATE_COMPANY")
    @Override
    public CompanyResponse create(CompanyRequest request) {
        Company company = new Company();
        mapToEntity(request, company);
        return mapToResponse(repository.save(company));
    }

    @Override
    public List<CompanyResponse> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getById(UUID id) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company not found"));
        return mapToResponse(company);
    }

    @Audit(action = "UPDATE_COMPANY")
    @Override
    public CompanyResponse update(UUID id, CompanyRequest request) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company not found"));
        mapToEntity(request, company);
        return mapToResponse(repository.save(company));
    }

    @Audit(action = "DELETE_COMPANY")
    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private CompanyResponse mapToResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setInn(company.getInn());
        response.setWebsite(company.getWebsite());
        response.setDescription(company.getDescription());
        return response;
    }

    private void mapToEntity(CompanyRequest request, Company company) {
        company.setName(request.getName());
        company.setInn(request.getInn());
        company.setWebsite(request.getWebsite());
        company.setDescription(request.getDescription());
    }
}
