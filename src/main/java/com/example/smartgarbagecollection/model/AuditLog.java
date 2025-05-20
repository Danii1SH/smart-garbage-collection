package com.example.smartgarbagecollection.model;

import com.example.smartgarbagecollection.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID performedByUserId;

    private String action;

    private String details;

    private LocalDateTime timestamp;

    private UUID performedByCompanyId;

    @Enumerated(EnumType.STRING)
    private Role performedByRole;
}
