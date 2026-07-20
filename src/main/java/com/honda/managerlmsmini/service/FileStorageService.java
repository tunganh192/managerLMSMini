package com.honda.managerlmsmini.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile store(MultipartFile file, String folder, boolean video);

    void deleteQuietly(String url);

    record StoredFile(String url, String contentType) {}
}
