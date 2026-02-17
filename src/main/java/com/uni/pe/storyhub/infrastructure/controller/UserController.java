package com.uni.pe.storyhub.infrastructure.controller;

import com.uni.pe.storyhub.application.dto.request.UpdateUserRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserProfileResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.application.port.in.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<UserResponse> response = userService.getProfile(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<UserResponse> response = userService.updateProfile(email, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/profile/image", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateProfileImage(
            @RequestParam("imagen_perfil") java.util.List<org.springframework.web.multipart.MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new com.uni.pe.storyhub.infrastructure.exception.BusinessException("La imagen es obligatoria", 0,
                    400);
        }
        if (files.size() > 1) {
            throw new com.uni.pe.storyhub.infrastructure.exception.BusinessException(
                    "Solo se permite subir una imagen a la vez", 0, 400);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<UserResponse> response = userService.updateProfileImage(email, files.get(0));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<PublicUserProfileResponse>> getPublicProfile(
            @PathVariable String username, Pageable pageable) {
        ApiResponse<PublicUserProfileResponse> response = userService.getPublicProfile(username, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
