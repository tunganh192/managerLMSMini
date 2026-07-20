package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.exception.*;
import com.honda.managerlmsmini.service.FileStorageService;
import java.io.*;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.*;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root;

    public FileStorageServiceImpl(@Value("${app.upload-dir:uploads}") String dir) {
        root = Path.of(dir).toAbsolutePath().normalize();
    }

    public StoredFile store(MultipartFile file, String folder, boolean video) {
        if (file == null || file.isEmpty()) throw new AppException(ErrorCode.FILE_EMPTY);
        Path dir = root.resolve(folder).normalize();
        if (!dir.startsWith(root)) throw new AppException(ErrorCode.FILE_INVALID_TYPE);
        try {
            Files.createDirectories(dir);
            try (BufferedInputStream in = new BufferedInputStream(file.getInputStream())) {
                in.mark(32);
                byte[] bytes = in.readNBytes(16);
                in.reset();
                String contentType;
                String extension;
                boolean detectedVideo;
                if (bytes.length >= 3
                        && Byte.toUnsignedInt(bytes[0]) == 0xFF
                        && Byte.toUnsignedInt(bytes[1]) == 0xD8
                        && Byte.toUnsignedInt(bytes[2]) == 0xFF) {
                    contentType = "image/jpeg";
                    extension = ".jpg";
                    detectedVideo = false;
                } else if (bytes.length >= 8
                        && Byte.toUnsignedInt(bytes[0]) == 0x89
                        && Byte.toUnsignedInt(bytes[1]) == 0x50
                        && Byte.toUnsignedInt(bytes[2]) == 0x4E
                        && Byte.toUnsignedInt(bytes[3]) == 0x47
                        && Byte.toUnsignedInt(bytes[4]) == 0x0D
                        && Byte.toUnsignedInt(bytes[5]) == 0x0A
                        && Byte.toUnsignedInt(bytes[6]) == 0x1A
                        && Byte.toUnsignedInt(bytes[7]) == 0x0A) {
                    contentType = "image/png";
                    extension = ".png";
                    detectedVideo = false;
                } else if (bytes.length >= 6
                        && bytes[0] == 'G'
                        && bytes[1] == 'I'
                        && bytes[2] == 'F'
                        && bytes[3] == '8'
                        && (bytes[4] == '7' || bytes[4] == '9')
                        && bytes[5] == 'a') {
                    contentType = "image/gif";
                    extension = ".gif";
                    detectedVideo = false;
                } else if (bytes.length >= 12
                        && bytes[0] == 'R'
                        && bytes[1] == 'I'
                        && bytes[2] == 'F'
                        && bytes[3] == 'F'
                        && bytes[8] == 'W'
                        && bytes[9] == 'E'
                        && bytes[10] == 'B'
                        && bytes[11] == 'P') {
                    contentType = "image/webp";
                    extension = ".webp";
                    detectedVideo = false;
                } else if (bytes.length >= 8
                        && bytes[4] == 'f'
                        && bytes[5] == 't'
                        && bytes[6] == 'y'
                        && bytes[7] == 'p') {
                    contentType = "video/mp4";
                    extension = ".mp4";
                    detectedVideo = true;
                } else if (bytes.length >= 4
                        && Byte.toUnsignedInt(bytes[0]) == 0x1A
                        && Byte.toUnsignedInt(bytes[1]) == 0x45
                        && Byte.toUnsignedInt(bytes[2]) == 0xDF
                        && Byte.toUnsignedInt(bytes[3]) == 0xA3) {
                    contentType = "video/webm";
                    extension = ".webm";
                    detectedVideo = true;
                } else if (bytes.length >= 4
                        && bytes[0] == 'O'
                        && bytes[1] == 'g'
                        && bytes[2] == 'g'
                        && bytes[3] == 'S') {
                    contentType = "video/ogg";
                    extension = ".ogv";
                    detectedVideo = true;
                } else {
                    throw new AppException(ErrorCode.FILE_INVALID_TYPE);
                }
                if (detectedVideo != video) throw new AppException(ErrorCode.FILE_INVALID_TYPE);
                String name = java.util.UUID.randomUUID() + extension;
                Path target = dir.resolve(name).normalize();
                if (!target.startsWith(root)) throw new AppException(ErrorCode.FILE_INVALID_TYPE);
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                String url = "/uploads/" + folder.replace('\\', '/') + "/" + name;
                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status != TransactionSynchronization.STATUS_COMMITTED) deleteQuietly(url);
                        }
                    });
                }
                return new StoredFile(url, contentType);
            }
        } catch (IOException exception) {
            throw new AppException(ErrorCode.FILE_STORE_ERROR, exception);
        }
    }

    public void deleteQuietly(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            Path target = root.resolve(url.substring("/uploads/".length())).normalize();
            if (target.startsWith(root)) Files.deleteIfExists(target);
        } catch (IOException ignored) {
        }
    }
}
