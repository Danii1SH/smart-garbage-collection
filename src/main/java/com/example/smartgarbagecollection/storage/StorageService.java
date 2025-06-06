package com.example.smartgarbagecollection.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface StorageService {
    String getFullPath(String imageName);
    boolean exists(String imageName);
}
