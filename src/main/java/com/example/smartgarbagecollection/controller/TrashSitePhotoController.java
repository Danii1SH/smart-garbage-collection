package com.example.smartgarbagecollection.controller;

import com.example.smartgarbagecollection.client.GigaChatAIClient;
import com.example.smartgarbagecollection.service.TrashSitePhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/trash-site-photos")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TrashSitePhotoController {

    private final TrashSitePhotoService photoService;
    private final GigaChatAIClient gigaChatAIClient;


    @GetMapping("/message")
    public ResponseEntity<String> sendTestMessage() {
        try {
            String result = gigaChatAIClient.testMessageOnly();
            return ResponseEntity.ok("Ответ GigaChat: " + result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/analyze-existing")
    public ResponseEntity<String> analyzeExistingPhoto(
            @RequestParam("trashSiteId") UUID trashSiteId,
            @RequestParam("fileName") String fileName
    ) throws IOException {
        File file = new File("uploads/images/" + fileName);
        photoService.uploadAndAnalyzeDirectory(trashSiteId, file);
        return ResponseEntity.ok("Анализ завершён");
    }

    @PostMapping("/upload")
    @Operation(summary = "Загрузка и анализ фото мусорной площадки")
    public ResponseEntity<Void> upload(
            @RequestParam UUID trashSiteId,
            @RequestPart MultipartFile image
    ) throws IOException {
        photoService.uploadAndAnalyze2(trashSiteId, image);
        return ResponseEntity.ok().build();
    }
}
