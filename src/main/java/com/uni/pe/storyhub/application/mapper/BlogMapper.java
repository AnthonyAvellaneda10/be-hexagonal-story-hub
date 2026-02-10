package com.uni.pe.storyhub.application.mapper;

import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { AuthMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "vistas", source = "vistasCount")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTagsToResponseList")
    BlogResponse toResponse(Blog blog);

    @Mapping(target = "vistasCount", constant = "0")
    @Mapping(target = "likes", constant = "0")
    @Mapping(target = "tags", ignore = true)
    Blog toEntity(BlogRequest request);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntityFromRequest(BlogRequest request, @MappingTarget Blog blog);

    @Named("mapTagsToResponseList")
    default List<TagResponse> mapTagsToResponseList(Set<Tag> tags) {
        if (tags == null)
            return Collections.emptyList();
        return tags.stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getIdTag())
                        .name(tag.getNombre())
                        .build())
                .sorted(Comparator.comparing(TagResponse::getId))
                .toList();
    }
}
