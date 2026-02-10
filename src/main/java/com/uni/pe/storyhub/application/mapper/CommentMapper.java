package com.uni.pe.storyhub.application.mapper;

import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;
import com.uni.pe.storyhub.domain.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { AuthMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "user", source = "user")
    CommentResponse toResponse(Comment comment);

    @Mapping(target = "idComentario", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "blog", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    public abstract Comment toEntity(CommentRequest request);
}
