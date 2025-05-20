package com.example.smartgarbagecollection.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
public class TrashSitePhotoUploadRequest {
    private UUID trashSiteId;
    private MultipartFile image;
}
