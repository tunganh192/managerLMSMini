package com.honda.managerlmsmini.service;

import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.enums.*;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void addFiles(
            ObjectType objectType,
            Long objectId,
            List<MultipartFile> files,
            MediaType mediaType,
            MediaRole mediaRole,
            String folder);

    void updateStatuses(ObjectType objectType, Long objectId, List<Long> deletedIds, List<Long> restoredIds);

    void softDeleteAll(ObjectType objectType, Long objectId);

    void softDeleteAll(ObjectType objectType, Collection<Long> objectIds);

    void softDeleteAll(ObjectType objectType, Long objectId, MediaRole mediaRole);

    void promoteMedia(
            ObjectType objectType,
            Long objectId,
            Long mediaId,
            MediaType expectedType,
            MediaRole primaryRole,
            MediaRole secondaryRole);

    List<ImageResponse> findActive(ObjectType objectType, Long objectId);

    Map<Long, List<ImageResponse>> findActive(ObjectType objectType, List<Long> objectIds);

    String findFirstUrl(List<ImageResponse> images, MediaRole role);

    long countActive(ObjectType objectType, Long objectId, MediaRole mediaRole);
}
