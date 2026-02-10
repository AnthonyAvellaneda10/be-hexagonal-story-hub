package com.uni.pe.storyhub.application.mapper;

import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.application.dto.response.PublicUserResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "fullName", source = "nombreCompleto")
    UserResponse toUserResponse(User user);

    @Mapping(target = "fullName", source = "nombreCompleto")
    PublicUserResponse toPublicUserResponse(User user);

    User toEntity(RegisterRequest registerRequest);
}
