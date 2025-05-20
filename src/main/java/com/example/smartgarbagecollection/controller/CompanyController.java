package com.example.smartgarbagecollection.controller;

import com.example.smartgarbagecollection.dto.CompanyRequest;
import com.example.smartgarbagecollection.dto.CompanyResponse;
import com.example.smartgarbagecollection.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Компании", description = "Управление компаниями")
public class CompanyController {

    private final CompanyService service;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Создание компании")
    public ResponseEntity<CompanyResponse> create(@RequestBody CompanyRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Operation(summary = "Получение всех компаний")
    public ResponseEntity<List<CompanyResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получение компании по ID")
    public ResponseEntity<CompanyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Обновление компании")
    public ResponseEntity<CompanyResponse> update(@PathVariable UUID id, @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление компании")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

