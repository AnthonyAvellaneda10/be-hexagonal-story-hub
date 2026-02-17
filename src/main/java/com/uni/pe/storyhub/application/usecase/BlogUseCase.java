package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateBlogRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import com.uni.pe.storyhub.application.mapper.BlogMapper;
import com.uni.pe.storyhub.application.port.in.BlogService;
import com.uni.pe.storyhub.application.util.ImageValidator;
import com.uni.pe.storyhub.domain.entity.*;
import com.uni.pe.storyhub.domain.repository.*;
import com.uni.pe.storyhub.domain.port.out.StoragePort;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlogUseCase implements BlogService {

        private final BlogRepository blogRepository;
        private final TagRepository tagRepository;
        private final UserRepository userRepository;
        private final BlogVistaRepository blogVistaRepository;
        private final LikeByUserRepository likeByUserRepository;
        private final BlogMapper blogMapper;
        private final ToastIdGenerator toastIdGenerator;
        private final StoragePort storagePort;
        private final ImageValidator imageValidator;

        public BlogUseCase(BlogRepository blogRepository, TagRepository tagRepository, UserRepository userRepository,
                        BlogVistaRepository blogVistaRepository, LikeByUserRepository likeByUserRepository,
                        BlogMapper blogMapper,
                        ToastIdGenerator toastIdGenerator,
                        StoragePort storagePort,
                        ImageValidator imageValidator) {
                this.blogRepository = blogRepository;
                this.tagRepository = tagRepository;
                this.userRepository = userRepository;
                this.blogVistaRepository = blogVistaRepository;
                this.likeByUserRepository = likeByUserRepository;
                this.blogMapper = blogMapper;
                this.toastIdGenerator = toastIdGenerator;
                this.storagePort = storagePort;
                this.imageValidator = imageValidator;
        }

        private static final String BLOG_NOT_FOUND = "Blog no encontrado";
        private static final String USER_NOT_FOUND = "Usuario no encontrado";
        private static final String SUCCESS_TYPE = ApiResponse.TYPE_SUCCESS;

        @Override
        @Transactional
        public ApiResponse<BlogResponse> createBlog(BlogRequest request,
                        org.springframework.web.multipart.MultipartFile imgBanner,
                        org.springframework.web.multipart.MultipartFile imgPortada,
                        String userEmail) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

                Blog blog = blogMapper.toEntity(request);
                blog.setAuthor(user);

                String slug = generateSlug(request.getTitulo());
                if (blogRepository.existsBySlugAndRemovedFalse(slug)) {
                        throw new BusinessException(
                                        "Ya existe un blog con este título o uno muy similar. Por favor, intenta con otro.",
                                        0, 400);
                }

                blog.setSlug(slug);
                blog.setTags(processTags(request.getTags()));
                blog.setUsuarioCreacion(userEmail);

                // Handle Images
                if (imgBanner != null && !imgBanner.isEmpty()) {
                        blog.setImgBanner(uploadBlogImage(imgBanner, "banners"));
                }
                if (imgPortada != null && !imgPortada.isEmpty()) {
                        blog.setImgPortada(uploadBlogImage(imgPortada, "portadas"));
                }

                Blog savedBlog = blogRepository.save(blog);
                return ApiResponse.<BlogResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Blog creado exitosamente")
                                .type(SUCCESS_TYPE)
                                .statusCode(201)
                                .data(enrichBlogResponseWithUrls(savedBlog, userEmail))
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<BlogResponse> updateBlog(Integer idBlog, UpdateBlogRequest request,
                        String userEmail) {
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));

                if (!blog.getAuthor().getEmail().equals(userEmail)) {
                        throw new BusinessException("No tienes permiso para editar este blog", 0, 403);
                }

                if (request.getTitulo() != null && !request.getTitulo().equals(blog.getTitulo())) {
                        String newSlug = generateSlug(request.getTitulo());
                        if (!newSlug.equals(blog.getSlug()) && blogRepository.existsBySlugAndRemovedFalse(newSlug)) {
                                throw new BusinessException(
                                                "Ya existe un blog con este título o uno muy similar. Por favor, intenta con otro.",
                                                0, 400);
                        }
                        blog.setTitulo(request.getTitulo());
                        blog.setSlug(newSlug);
                }

                if (request.getBreveDescripcion() != null)
                        blog.setBreveDescripcion(request.getBreveDescripcion());
                if (request.getContenidoBlog() != null)
                        blog.setContenidoBlog(request.getContenidoBlog());
                if (request.getPublicado() != null)
                        blog.setPublicado(request.getPublicado());
                if (request.getDescripcionImgPortada() != null)
                        blog.setDescripcionImgPortada(request.getDescripcionImgPortada());
                if (request.getTags() != null)
                        blog.setTags(processTags(request.getTags()));

                blog.setUsuarioActualizacion(userEmail);

                Blog updatedBlog = blogRepository.save(blog);
                return ApiResponse.<BlogResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Blog actualizado exitosamente")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(enrichBlogResponseWithUrls(updatedBlog, userEmail))
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<BlogResponse> updateBlogBanner(Integer idBlog,
                        org.springframework.web.multipart.MultipartFile imgBanner,
                        String userEmail) {
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));

                if (!blog.getAuthor().getEmail().equals(userEmail)) {
                        throw new BusinessException("No tienes permiso para editar este blog", 0, 403);
                }

                if (imgBanner != null && !imgBanner.isEmpty()) {
                        deleteOldImage(blog.getImgBanner());
                        blog.setImgBanner(uploadBlogImage(imgBanner, "banners"));
                }

                blog.setUsuarioActualizacion(userEmail);
                Blog updatedBlog = blogRepository.save(blog);

                return ApiResponse.<BlogResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Banner de blog actualizado")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(enrichBlogResponseWithUrls(updatedBlog, userEmail))
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<BlogResponse> updateBlogPortada(Integer idBlog,
                        org.springframework.web.multipart.MultipartFile imgPortada,
                        String userEmail) {
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));

                if (!blog.getAuthor().getEmail().equals(userEmail)) {
                        throw new BusinessException("No tienes permiso para editar este blog", 0, 403);
                }

                if (imgPortada != null && !imgPortada.isEmpty()) {
                        deleteOldImage(blog.getImgPortada());
                        blog.setImgPortada(uploadBlogImage(imgPortada, "portadas"));
                }

                blog.setUsuarioActualizacion(userEmail);
                Blog updatedBlog = blogRepository.save(blog);

                return ApiResponse.<BlogResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Portada de blog actualizada")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(enrichBlogResponseWithUrls(updatedBlog, userEmail))
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<Void> deleteBlog(Integer idBlog, String userEmail) {
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));

                if (!blog.getAuthor().getEmail().equals(userEmail)) {
                        throw new BusinessException("No tienes permiso para eliminar este blog", 0, 403);
                }

                blog.setRemoved(true);
                blogRepository.save(blog);

                return ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Blog eliminado exitosamente")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<BlogResponse> getBlogBySlug(String slug, String ipAddress, String userAgent,
                        String userEmail) {
                Blog blog = blogRepository.findBySlugAndRemovedFalse(slug)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));

                if (!blog.isPublicado()) {
                        throw new BusinessException("Este blog aún no se encuentra disponible de manera pública.", 0,
                                        403);
                }

                // Logic for unique views
                boolean alreadyViewed;
                User visitor = null;
                if (userEmail != null && !userEmail.equals("anonymousUser")) {
                        visitor = userRepository.findByEmail(userEmail).orElse(null);
                }

                if (visitor != null) {
                        alreadyViewed = blogVistaRepository.existsByBlogAndUser(blog, visitor);
                } else {
                        alreadyViewed = blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(blog,
                                        ipAddress, userAgent);
                }

                if (!alreadyViewed) {
                        BlogVista vista = BlogVista.builder()
                                        .blog(blog)
                                        .user(visitor)
                                        .ipAddress(ipAddress)
                                        .userAgent(userAgent)
                                        .build();
                        blogVistaRepository.save(vista);

                        blog.setVistasCount(blog.getVistasCount() + 1);
                        blogRepository.save(blog);
                }

                BlogResponse blogResponse = blogMapper.toResponse(blog);
                blogResponse.setLiked(checkIfUserLikedBlog(blog, userEmail));

                return ApiResponse.<BlogResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Blog recuperado")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(enrichBlogResponseWithUrls(blog, userEmail))
                                .build();
        }

        @Override
        public ApiResponse<Page<BlogResponse>> getAllPublicBlogs(Pageable pageable, String userEmail) {
                Page<Blog> blogs = blogRepository.findByPublicadoTrueAndRemovedFalse(pageable);

                Page<BlogResponse> blogResponses = blogs.map(blog -> enrichBlogResponseWithUrls(blog, userEmail));

                return ApiResponse.<Page<BlogResponse>>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Lista de blogs públicos")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(blogResponses)
                                .build();
        }

        @Override
        public ApiResponse<Page<BlogResponse>> getBlogsByUser(String email, Pageable pageable,
                        String currentUserEmail) {
                Page<Blog> blogs = blogRepository.findByAuthorEmailAndRemovedFalse(email, pageable);

                Page<BlogResponse> blogResponses = blogs
                                .map(blog -> enrichBlogResponseWithUrls(blog, currentUserEmail));

                return ApiResponse.<Page<BlogResponse>>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Tus blogs")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(blogResponses)
                                .build();
        }

        @Override
        public ApiResponse<List<TagResponse>> getAllTags() {
                List<TagResponse> tags = tagRepository.findAll().stream()
                                .map(tag -> TagResponse.builder()
                                                .id(tag.getIdTag())
                                                .name(tag.getNombre())
                                                .build())
                                .sorted(Comparator.comparing(TagResponse::getId))
                                .toList();

                return ApiResponse.<List<TagResponse>>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Lista de etiquetas")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(tags)
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<Void> giveLike(Integer idBlog, String userEmail) {
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

                LikeByUser like = likeByUserRepository.findByUserAndBlog(user, blog)
                                .orElseGet(() -> LikeByUser.builder()
                                                .user(user)
                                                .blog(blog)
                                                .isLike(false)
                                                .build());

                if (like.isLike()) {
                        // Toggle OFF
                        like.setLike(false);
                        blog.setLikes(Math.max(0, blog.getLikes() - 1));
                } else {
                        // Toggle ON
                        like.setLike(true);
                        blog.setLikes(blog.getLikes() + 1);
                }

                likeByUserRepository.save(like);
                blogRepository.save(blog);

                String message = like.isLike() ? "Like registrado" : "Like eliminado";

                return ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message(message)
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .build();
        }

        private boolean checkIfUserLikedBlog(Blog blog, String userEmail) {
                if (userEmail == null || userEmail.equals("anonymousUser")) {
                        return false;
                }
                return userRepository.findByEmail(userEmail)
                                .map(user -> likeByUserRepository.findByUserAndBlog(user, blog)
                                                .map(LikeByUser::isLike)
                                                .orElse(false))
                                .orElse(false);
        }

        private String uploadBlogImage(org.springframework.web.multipart.MultipartFile file, String folder) {
                imageValidator.validate(file, folder);
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String key = "blogs/" + folder + "/" + System.currentTimeMillis() + "_"
                                + java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
                try {
                        storagePort.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
                } catch (java.io.IOException e) {
                        throw new BusinessException("Error al procesar la imagen del blog", 0, 500);
                }
                return key;
        }

        private void deleteOldImage(String key) {
                if (key != null && !key.startsWith("http")) {
                        try {
                                storagePort.deleteFile(key);
                        } catch (Exception e) {
                                // Ignore error
                        }
                }
        }

        private BlogResponse enrichBlogResponseWithUrls(Blog blog, String userEmail) {
                BlogResponse response = blogMapper.toResponse(blog);
                response.setLiked(checkIfUserLikedBlog(blog, userEmail));

                if (blog.getImgBanner() != null && !blog.getImgBanner().startsWith("http")) {
                        response.setImgBanner(storagePort.generatePresignedUrl(blog.getImgBanner(), 10));
                }
                if (blog.getImgPortada() != null && !blog.getImgPortada().startsWith("http")) {
                        response.setImgPortada(storagePort.generatePresignedUrl(blog.getImgPortada(), 10));
                }

                // Enrriquecer también la imagen del autor si existe
                if (blog.getAuthor() != null && blog.getAuthor().getImagenPerfil() != null
                                && !blog.getAuthor().getImagenPerfil().startsWith("http")) {
                        response.getAuthor().setImagenPerfil(
                                        storagePort.generatePresignedUrl(blog.getAuthor().getImagenPerfil(), 10));
                }

                return response;
        }

        private String generateSlug(String titulo) {
                return titulo.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-").replaceAll("(^-)|(-$)",
                                "");
        }

        private Set<Tag> processTags(List<String> tagNames) {
                if (tagNames == null)
                        return new HashSet<>();
                return tagNames.stream().map(name -> tagRepository.findByNombre(name)
                                .orElseGet(() -> tagRepository.save(Tag.builder().nombre(name).build())))
                                .collect(Collectors.toSet());
        }
}
