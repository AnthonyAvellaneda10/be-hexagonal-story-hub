package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.dto.request.UpdateUserRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserProfileResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.application.mapper.AuthMapper;
import com.uni.pe.storyhub.application.mapper.BlogMapper;
import com.uni.pe.storyhub.application.port.in.UserService;
import com.uni.pe.storyhub.application.util.ImageValidator;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.BlogRepository;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.domain.port.out.StoragePort;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUseCase implements UserService {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final AuthMapper authMapper;
    private final BlogMapper blogMapper;
    private final ToastIdGenerator toastIdGenerator;
    private final StoragePort storagePort;
    private final ImageValidator imageValidator;

    public UserUseCase(UserRepository userRepository, BlogRepository blogRepository, AuthMapper authMapper,
            BlogMapper blogMapper, ToastIdGenerator toastIdGenerator, StoragePort storagePort,
            ImageValidator imageValidator) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.authMapper = authMapper;
        this.blogMapper = blogMapper;
        this.toastIdGenerator = toastIdGenerator;
        this.storagePort = storagePort;
        this.imageValidator = imageValidator;
    }

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
                .data(enrichUserResponseWithUrl(user))
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
                .data(enrichUserResponseWithUrl(user))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserResponse> updateProfileImage(String email,
            org.springframework.web.multipart.MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        imageValidator.validate(file, "imagen_perfil");

        String originalFilename = file.getOriginalFilename();

        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String key = "profiles/" + user.getIdUsuario() + "/profile_" + System.currentTimeMillis() + extension;
        try {
            storagePort.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (java.io.IOException e) {
            throw new BusinessException("Error al procesar la imagen", 0, 500);
        }

        // Delete old image if exists
        if (user.getImagenPerfil() != null && !user.getImagenPerfil().startsWith("http")) {
            try {
                storagePort.deleteFile(user.getImagenPerfil());
            } catch (Exception e) {
                // Ignore error on delete
            }
        }

        user.setImagenPerfil(key);
        user.setFechaActualizacion(java.time.LocalDateTime.now());
        userRepository.save(user);

        return ApiResponse.<UserResponse>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Foto de perfil actualizada exitosamente")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .data(enrichUserResponseWithUrl(user))
                .build();
    }

    private UserResponse enrichUserResponseWithUrl(User user) {
        UserResponse response = authMapper.toUserResponse(user);
        if (user.getImagenPerfil() != null && !user.getImagenPerfil().startsWith("http")) {
            response.setImagenPerfil(storagePort.generatePresignedUrl(user.getImagenPerfil(), 10));
        }
        return response;
    }

    private PublicUserResponse enrichPublicUserResponseWithUrl(User user) {
        PublicUserResponse response = authMapper.toPublicUserResponse(user);
        if (user.getImagenPerfil() != null && !user.getImagenPerfil().startsWith("http")) {
            response.setImagenPerfil(storagePort.generatePresignedUrl(user.getImagenPerfil(), 10));
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PublicUserProfileResponse> getPublicProfile(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        PublicUserResponse userResponse = enrichPublicUserResponseWithUrl(user);

        Page<Blog> blogsPage = blogRepository.findByAuthorUsernameAndPublicadoTrueAndRemovedFalse(username, pageable);
        Page<BlogResponse> blogResponses = blogsPage.map(blog -> {
            BlogResponse res = blogMapper.toResponse(blog);
            if (blog.getImgBanner() != null && !blog.getImgBanner().startsWith("http")) {
                res.setImgBanner(storagePort.generatePresignedUrl(blog.getImgBanner(), 10));
            }
            if (blog.getImgPortada() != null && !blog.getImgPortada().startsWith("http")) {
                res.setImgPortada(storagePort.generatePresignedUrl(blog.getImgPortada(), 10));
            }
            return res;
        });

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
