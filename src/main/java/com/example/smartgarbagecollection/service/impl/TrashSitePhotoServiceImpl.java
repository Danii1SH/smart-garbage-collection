package com.example.smartgarbagecollection.service.impl;

import com.example.smartgarbagecollection.client.GigaChatAIClient;
import com.example.smartgarbagecollection.dto.TrashSitePhotoUploadRequest;
import com.example.smartgarbagecollection.model.TrashSite;
import com.example.smartgarbagecollection.model.TrashSitePhoto;
import com.example.smartgarbagecollection.repository.TrashSitePhotoRepository;
import com.example.smartgarbagecollection.repository.TrashSiteRepository;
import com.example.smartgarbagecollection.service.TrashSitePhotoService;
import com.example.smartgarbagecollection.storage.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrashSitePhotoServiceImpl implements TrashSitePhotoService {

    private final TrashSitePhotoRepository photoRepository;
    private final TrashSiteRepository trashSiteRepository;
    private final StorageService storageService;
    private final GigaChatAIClient gigaChatAIClient;

    @Override
    public void uploadAndAnalyzeDirectory(UUID trashSiteId, File imageFile) throws IOException {
        TrashSite trashSite = trashSiteRepository.findById(trashSiteId)
                .orElseThrow(() -> new NoSuchElementException("Trash site not found"));

        String aiStatus = gigaChatAIClient.analyzeImage(imageFile);

        TrashSitePhoto photo = new TrashSitePhoto();
        photo.setTrashSite(trashSite);

        String relativePath = storageService.getRelativePath(imageFile);
        photo.setImagePath(relativePath);

        photo.setAiStatus(aiStatus);
        photo.setUploadedAt(LocalDateTime.now());

        photoRepository.save(photo);

        // Статус обновляется напрямую
        trashSite.setStatus(aiStatus);
        trashSiteRepository.save(trashSite);

        log.info("Saved image relative path: " + relativePath);
    }

    @Override
    public void uploadAndAnalyze2(UUID trashSiteId, MultipartFile image) throws IOException {
        TrashSite trashSite = trashSiteRepository.findById(trashSiteId)
                .orElseThrow(() -> new NoSuchElementException("Trash site not found"));

        String savedPath = storageService.save(image);

        String aiStatus = gigaChatAIClient.analyzeImage(new File(savedPath));

        TrashSitePhoto photo = new TrashSitePhoto();
        photo.setTrashSite(trashSite);
        photo.setImagePath(savedPath);
        photo.setAiStatus(aiStatus);
        photoRepository.save(photo);

        trashSite.setStatus(aiStatus);
        trashSiteRepository.save(trashSite);
    }
}
