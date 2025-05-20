package com.example.smartgarbagecollection.repository;

import com.example.smartgarbagecollection.model.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCompanyRepository extends JpaRepository<UserCompany, UUID> {
    Optional<UserCompany> findByUserId(UUID userId);

    void deleteByUserId(UUID id);
}