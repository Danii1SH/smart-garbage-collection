package com.example.smartgarbagecollection.storage.impl;

import com.example.smartgarbagecollection.storage.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    @Value("${app.storage.image-dir}")
    private String imageDir;

    @Override
    public String getFullPath(String imageName) {
        Path fullPath = Paths.get(imageDir, imageName).toAbsolutePath();
        return fullPath.toString();
    }

    @Override
    public boolean exists(String imageName) {
        boolean exists = Files.exists(Paths.get(imageDir, imageName));
        return exists;
    }
}

