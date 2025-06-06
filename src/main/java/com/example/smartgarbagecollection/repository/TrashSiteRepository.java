package com.example.smartgarbagecollection.repository;

import com.example.smartgarbagecollection.model.Company;
import com.example.smartgarbagecollection.model.TrashSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrashSiteRepository extends JpaRepository<TrashSite, UUID> {
    List<TrashSite> findAllByCompanyId(UUID companyId);
    //индекс повесить на companyId
}
