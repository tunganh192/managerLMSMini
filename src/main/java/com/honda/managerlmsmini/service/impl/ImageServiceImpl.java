package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.entity.Image;
import com.honda.managerlmsmini.enums.*;
import com.honda.managerlmsmini.exception.*;
import com.honda.managerlmsmini.mapper.ImageMapper;
import com.honda.managerlmsmini.repository.ImageRepository;
import com.honda.managerlmsmini.service.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageServiceImpl implements ImageService {
    ImageRepository imageRepository;
    ImageMapper imageMapper;
    FileStorageService fileStorageService;

    @Override
    @Transactional
    public void addFiles(
            ObjectType objectType,
            Long objectId,
            List<MultipartFile> files,
            MediaType mediaType,
            MediaRole mediaRole,
            String folder) {
        if (files == null) return;
        files.stream().filter(file -> file != null && !file.isEmpty()).forEach(file -> {
            FileStorageService.StoredFile storedFile =
                    fileStorageService.store(file, folder, mediaType == MediaType.VIDEO);
            Image image = new Image();
            image.setObjectType(objectType);
            image.setObjectId(objectId);
            image.setMediaType(mediaType);
            image.setMediaRole(mediaRole);
            image.setUrl(storedFile.url());
            image.setOriginalName(file.getOriginalFilename());
            image.setContentType(storedFile.contentType());
            imageRepository.save(image);
        });
    }

    @Override
    @Transactional
    public void updateStatuses(ObjectType objectType, Long objectId, List<Long> deletedIds, List<Long> restoredIds) {
        if (deletedIds != null && !deletedIds.isEmpty()) {
            imageRepository.updateStatusByIds(objectType, objectId, new LinkedHashSet<>(deletedIds), 0);
        }
        if (restoredIds != null && !restoredIds.isEmpty()) {
            imageRepository.updateStatusByIds(objectType, objectId, new LinkedHashSet<>(restoredIds), 1);
        }
    }

    @Override
    @Transactional
    public void softDeleteAll(ObjectType objectType, Long objectId) {
        imageRepository.softDeleteByObject(objectType, objectId);
    }

    @Override
    @Transactional
    public void softDeleteAll(ObjectType objectType, Collection<Long> objectIds) {
        if (objectIds == null || objectIds.isEmpty()) return;
        imageRepository.softDeleteByObjects(objectType, new LinkedHashSet<>(objectIds));
    }

    @Override
    @Transactional
    public void softDeleteAll(ObjectType objectType, Long objectId, MediaRole mediaRole) {
        imageRepository.softDeleteByRole(objectType, objectId, mediaRole);
    }

    @Override
    @Transactional
    public void promoteMedia(
            ObjectType objectType,
            Long objectId,
            Long mediaId,
            MediaType expectedType,
            MediaRole primaryRole,
            MediaRole secondaryRole) {
        Image selected = imageRepository
                .findByIdAndObjectTypeAndObjectIdAndStatus(mediaId, objectType, objectId, 1)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_INVALID_TYPE));
        if (selected.getMediaType() != expectedType) throw new AppException(ErrorCode.FILE_INVALID_TYPE);
        imageRepository.demoteCurrentPrimary(objectType, objectId, selected.getId(), primaryRole, secondaryRole);
        selected.setMediaRole(primaryRole);
    }

    @Override
    public List<ImageResponse> findActive(ObjectType objectType, Long objectId) {
        return imageRepository.findByObjectTypeAndObjectIdAndStatusOrderByIdAsc(objectType, objectId, 1).stream()
                .map(image -> {
                    ImageResponse response = imageMapper.toResponse(image);
                    if (response.getUrl() != null && !response.getUrl().isBlank()) {
                        response.setUrl(ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path(response.getUrl())
                                .toUriString());
                    }
                    return response;
                })
                .toList();
    }

    @Override
    public Map<Long, List<ImageResponse>> findActive(ObjectType objectType, List<Long> objectIds) {
        if (objectIds == null || objectIds.isEmpty()) return Map.of();
        return imageRepository.findActiveByObjects(objectType, objectIds, 1).stream()
                .collect(Collectors.groupingBy(
                        Image::getObjectId,
                        Collectors.mapping(
                                image -> {
                                    ImageResponse response = imageMapper.toResponse(image);
                                    if (response.getUrl() != null
                                            && !response.getUrl().isBlank()) {
                                        response.setUrl(ServletUriComponentsBuilder.fromCurrentContextPath()
                                                .path(response.getUrl())
                                                .toUriString());
                                    }
                                    return response;
                                },
                                Collectors.toList())));
    }

    @Override
    public String findFirstUrl(List<ImageResponse> images, MediaRole role) {
        return images.stream()
                .filter(image -> role.name().equals(image.getMediaRole()))
                .map(ImageResponse::getUrl)
                .reduce((first, last) -> last)
                .orElse(null);
    }

    @Override
    public long countActive(ObjectType objectType, Long objectId, MediaRole mediaRole) {
        return imageRepository.countByObjectTypeAndObjectIdAndMediaRoleAndStatus(objectType, objectId, mediaRole, 1);
    }
}
