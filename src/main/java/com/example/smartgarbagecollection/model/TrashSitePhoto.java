package com.example.smartgarbagecollection.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trash_site_photo")
public class TrashSitePhoto {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trash_site_id", nullable = false)
    private TrashSite trashSite;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "ai_status")
    private String aiStatus;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
