package com.example.smartgarbagecollection.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface StorageService {
    String save(MultipartFile file) throws IOException;
    String getRelativePath(File file);
    File getFile(String relativePath);
}
