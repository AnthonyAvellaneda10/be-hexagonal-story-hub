package com.uni.pe.storyhub.application.port.in;

import com.uni.pe.storyhub.application.dto.request.UpdateUserRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserProfileResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ApiResponse<UserResponse> getProfile(String email);

    ApiResponse<UserResponse> updateProfile(String email, UpdateUserRequest request);

    ApiResponse<PublicUserProfileResponse> getPublicProfile(String username, Pageable pageable);
}
