package com.uni.pe.storyhub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicUserProfileResponse {
    private PublicUserResponse user;
    private Page<BlogResponse> blogs;
}
