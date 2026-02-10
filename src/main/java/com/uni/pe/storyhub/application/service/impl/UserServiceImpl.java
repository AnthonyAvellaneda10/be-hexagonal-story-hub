package com.uni.pe.storyhub.application.service.impl;

import com.uni.pe.storyhub.application.dto.request.UpdateUserRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserProfileResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.application.mapper.AuthMapper;
import com.uni.pe.storyhub.application.mapper.BlogMapper;
import com.uni.pe.storyhub.application.service.UserService;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.BlogRepository;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final AuthMapper authMapper;
    private final BlogMapper blogMapper;
    private final ToastIdGenerator toastIdGenerator;

    private static final String USER_NOT_FOUND = "Usuario no encontrado";
    private static final String SUCCESS_TYPE = ApiResponse.TYPE_SUCCESS;

    @Override
    public ApiResponse<UserResponse> getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        return ApiResponse.<UserResponse>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Perfil recuperado")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .data(authMapper.toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserResponse> updateProfile(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException("El nombre de usuario ya está en uso. Por favor, elige otro.", 0, 400);
            }
            user.setUsername(request.getUsername());
        }

        if (request.getDescripcion() != null)
            user.setDescripcion(request.getDescripcion());
        if (request.getLinkedin() != null)
            user.setLinkedin(request.getLinkedin());
        if (request.getTelegram() != null)
            user.setTelegram(request.getTelegram());
        if (request.getInstagram() != null)
            user.setInstagram(request.getInstagram());
        if (request.getFacebook() != null)
            user.setFacebook(request.getFacebook());
        if (request.getYoutube() != null)
            user.setYoutube(request.getYoutube());

        userRepository.save(user);

        return ApiResponse.<UserResponse>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Perfil actualizado exitosamente")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .data(authMapper.toUserResponse(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PublicUserProfileResponse> getPublicProfile(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        PublicUserResponse userResponse = authMapper.toPublicUserResponse(user);

        Page<Blog> blogsPage = blogRepository.findByAuthor_UsernameAndPublicadoTrueAndRemovedFalse(username, pageable);
        Page<BlogResponse> blogResponses = blogsPage.map(blogMapper::toResponse);

        PublicUserProfileResponse data = PublicUserProfileResponse.builder()
                .user(userResponse)
                .blogs(blogResponses)
                .build();

        return ApiResponse.<PublicUserProfileResponse>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Perfil público recuperado")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .data(data)
                .build();
    }
}
