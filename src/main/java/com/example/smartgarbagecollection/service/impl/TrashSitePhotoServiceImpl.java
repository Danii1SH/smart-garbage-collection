package com.example.smartgarbagecollection.service.impl;

import com.example.smartgarbagecollection.client.GeminiRestApiClient;
import com.example.smartgarbagecollection.model.TrashSite;
import com.example.smartgarbagecollection.model.TrashSitePhoto;
import com.example.smartgarbagecollection.repository.TrashSitePhotoRepository;
import com.example.smartgarbagecollection.repository.TrashSiteRepository;
import com.example.smartgarbagecollection.service.TrashSitePhotoService;
import com.example.smartgarbagecollection.storage.impl.LocalStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrashSitePhotoServiceImpl implements TrashSitePhotoService {

    private final TrashSitePhotoRepository photoRepository;
    private final TrashSiteRepository trashSiteRepository;
    private final LocalStorageService storageService;
    private final GeminiRestApiClient geminiClient;

    private static final String PROMPT = "Оцени заполненность контейнера. Ответь FULL или EMPTY.";

    @Transactional
    public void analyzeAndSavePhoto(UUID trashSiteId, String imageName) {
        if (!storageService.exists(imageName)) {
            throw new IllegalArgumentException("Image not found: " + imageName);
        }

        TrashSite trashSite = trashSiteRepository.findById(trashSiteId)
                .orElseThrow(() -> new EntityNotFoundException("Trash site not found: " + trashSiteId));

        // Получаем полный путь к файлу
        String fullPath = storageService.getFullPath(imageName);

        // Отправляем на анализ
        String aiStatus;
        try {
            aiStatus = geminiClient.sendContentWithImage(PROMPT, fullPath).toUpperCase().trim();
            if (!aiStatus.equals("FULL") && !aiStatus.equals("EMPTY")) {
                aiStatus = "UNKNOWN";
            }
        } catch (IOException e) {
            aiStatus = "UNKNOWN";
        }

        // Сохраняем фото и статус
        TrashSitePhoto photo = new TrashSitePhoto();
        photo.setTrashSite(trashSite);
        photo.setImagePath(imageName);
        photo.setAiStatus(aiStatus);
        photo.setUploadedAt(LocalDateTime.now());
        photoRepository.save(photo);

        trashSite.setStatus(aiStatus);
        trashSiteRepository.save(trashSite);
    }
}

