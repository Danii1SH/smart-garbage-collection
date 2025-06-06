package com.example.smartgarbagecollection.controller;

import com.example.smartgarbagecollection.dto.TrashSiteRequest;
import com.example.smartgarbagecollection.dto.TrashSiteResponse;
import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.security.AccessValidator;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import com.example.smartgarbagecollection.service.TrashSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trash-sites")
@Tag(name = "Мусорные площадки", description = "Управление мусорными площадками")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
@RequiredArgsConstructor
public class TrashSiteController {

    private final AccessValidator accessValidator;
    private final TrashSiteService service;

    @PostMapping
    @Operation(summary = "Создание мусорной площадки", description = "Создание новой мусорной площадки с данными из запроса")
    public ResponseEntity<TrashSiteResponse> create(@RequestBody TrashSiteRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    @Operation(
            summary = "Получение списка площадок",
            description = """
        Возвращает список всех мусорных площадок.
        - ADMIN: получает площадки всех компаний.
        - EDITOR и VIEWER: получают площадки только своей компании.
        """
    )
    public ResponseEntity<List<TrashSiteResponse>> getAllByCompany(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<TrashSiteResponse> trashSites;
        trashSites = accessValidator.isAdmin(userDetails)
                ? service.getAll()
                : service.getAllByCompany(userDetails.getCompanyId());
        return ResponseEntity.ok(trashSites);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Получение площадки по ID", description = "Получение информации о конкретной мусорной площадке по её ID")
    public ResponseEntity<TrashSiteResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление площадки", description = "Обновление информации о мусорной площадке по ID")
    public ResponseEntity<TrashSiteResponse> update(@PathVariable UUID id, @RequestBody TrashSiteRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление площадки", description = "Удаление мусорной площадки по её ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
