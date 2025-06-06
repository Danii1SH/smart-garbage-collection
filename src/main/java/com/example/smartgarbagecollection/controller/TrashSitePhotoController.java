package com.example.smartgarbagecollection.controller;

import com.example.smartgarbagecollection.client.GeminiRestApiClient;
import com.example.smartgarbagecollection.service.TrashSitePhotoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trash-site-photos")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Gemini", description = "#")
public class TrashSitePhotoController {

    private final TrashSitePhotoService photoService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzePhoto(
            @RequestParam UUID trashSiteId,
            @RequestParam String imageName) {
        try {
            photoService.analyzeAndSavePhoto(trashSiteId, imageName);
            return ResponseEntity.ok("Analysis complete and saved.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

//    @PostMapping("/analyze-existing")
//    public ResponseEntity<String> analyzeExistingPhoto(
//            @RequestParam("trashSiteId") UUID trashSiteId,
//            @RequestParam("fileName") String fileName
//    ) throws IOException {
//        File file = new File("uploads/images/" + fileName);
//        photoService.uploadAndAnalyzeDirectory(trashSiteId, file);
//        return ResponseEntity.ok("Анализ завершён");
//    }
//
//    @PostMapping("/upload")
//    @Operation(summary = "Загрузка и анализ фото мусорной площадки")
//    public ResponseEntity<Void> upload(
//            @RequestParam UUID trashSiteId,
//            @RequestPart MultipartFile image
//    ) throws IOException {
//        photoService.uploadAndAnalyze2(trashSiteId, image);
//        return ResponseEntity.ok().build();
//    }
}
