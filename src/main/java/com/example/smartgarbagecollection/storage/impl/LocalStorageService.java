package com.example.smartgarbagecollection.storage.impl;

import com.example.smartgarbagecollection.storage.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService implements StorageService {

    @Value("${app.storage.image-dir}")
    private String imageDir;

    private File absoluteImageDir;

    @PostConstruct
    public void init() {
        Path workingDir = Paths.get("").toAbsolutePath();
        Path fullPath = workingDir.resolve(imageDir).normalize();
        absoluteImageDir = fullPath.toFile();

        if (!absoluteImageDir.exists()) {
            absoluteImageDir.mkdirs();
        }

        log.info("Image directory resolved to: " + absoluteImageDir.getAbsolutePath());
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(absoluteImageDir, filename);
        file.transferTo(destination);

        // Возвращаем относительный путь для хранения в БД
        return imageDir.replace("\\", "/").replaceAll("^/+", "") + filename;
    }

    @Override
    public String getRelativePath(File file) {
        Path filePath = file.toPath().toAbsolutePath().normalize();
        Path basePath = absoluteImageDir.toPath().toAbsolutePath().normalize();

        if (!filePath.startsWith(basePath)) {
            throw new IllegalArgumentException("File is not inside the configured image directory");
        }

        String relativeToImages = basePath.relativize(filePath).toString().replace(File.separator, "/");
        return imageDir.replace("\\", "/").replaceAll("^/+", "") + relativeToImages;
    }

    @Override
    public File getFile(String relativePath) {
        return new File(Paths.get("").toAbsolutePath().resolve(relativePath).toFile().getAbsolutePath());
    }
}
