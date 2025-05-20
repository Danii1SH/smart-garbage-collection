package com.example.smartgarbagecollection.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public interface TrashSitePhotoService {
    void uploadAndAnalyzeDirectory(UUID trashSiteId, File imageFile)throws IOException;
    void uploadAndAnalyze2(UUID trashSiteId, MultipartFile image) throws IOException;
}