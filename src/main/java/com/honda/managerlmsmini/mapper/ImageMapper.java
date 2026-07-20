package com.honda.managerlmsmini.mapper;

import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.entity.Image;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {
    @Mapping(target = "objectType", expression = "java(image.getObjectType().name())")
    @Mapping(target = "mediaType", expression = "java(image.getMediaType().name())")
    @Mapping(target = "mediaRole", expression = "java(image.getMediaRole().name())")
    ImageResponse toResponse(Image image);
}
