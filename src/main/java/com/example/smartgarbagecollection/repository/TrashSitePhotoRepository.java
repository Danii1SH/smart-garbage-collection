package com.example.smartgarbagecollection.repository;

import com.example.smartgarbagecollection.model.TrashSitePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrashSitePhotoRepository extends JpaRepository<TrashSitePhoto, UUID> {
}
