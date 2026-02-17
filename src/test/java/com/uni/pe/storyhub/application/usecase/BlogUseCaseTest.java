package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateBlogRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import com.uni.pe.storyhub.application.mapper.BlogMapper;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.LikeByUser;
import com.uni.pe.storyhub.domain.entity.Tag;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.*;
import com.uni.pe.storyhub.application.util.ImageValidator;
import com.uni.pe.storyhub.domain.port.out.StoragePort;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogUseCaseTest {

    @Mock
    private BlogRepository blogRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlogVistaRepository blogVistaRepository;
    @Mock
    private LikeByUserRepository likeByUserRepository;
    @Mock
    private BlogMapper blogMapper;
    @Mock
    private ToastIdGenerator toastIdGenerator;
    @Mock
    private StoragePort storagePort;
    @Mock
    private ImageValidator imageValidator;

    @InjectMocks
    private BlogUseCase blogService;

    private User user;
    private Blog blog;
    private BlogRequest blogRequest;
    private UpdateBlogRequest updateBlogRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("author@test.com");
        user.setIdUsuario(1);

        blog = new Blog();
        blog.setIdBlog(1);
        blog.setTitulo("Test Title");
        blog.setSlug("test-title");
        blog.setAuthor(user);
        blog.setPublicado(true);
        blog.setVistasCount(0);
        blog.setLikes(0);

        blogRequest = new BlogRequest();
        blogRequest.setTitulo("Test Title");
        blogRequest.setTags(Collections.singletonList("java"));

        updateBlogRequest = new UpdateBlogRequest();
        updateBlogRequest.setTitulo("Test Title");
        updateBlogRequest.setContenidoBlog("Test Content");

        lenient().when(blogMapper.toResponse(any())).thenReturn(new BlogResponse());
    }

    @Test
    void createBlog_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogMapper.toEntity(any())).thenReturn(blog);
        when(blogRepository.existsBySlugAndRemovedFalse(anyString())).thenReturn(false);
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.createBlog(blogRequest, null, null, "author@test.com");

        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        verify(blogRepository, times(1)).save(any(Blog.class));
    }

    @Test
    void createBlog_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.createBlog(blogRequest, null, null, "author@test.com"));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void createBlog_SlugAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogMapper.toEntity(any())).thenReturn(blog);
        when(blogRepository.existsBySlugAndRemovedFalse(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.createBlog(blogRequest, null, null, "author@test.com"));
        assertEquals("Ya existe un blog con este título o uno muy similar. Por favor, intenta con otro.",
                exception.getMessage());
    }

    @Test
    void updateBlog_Success() {
        updateBlogRequest.setTitulo("Completely New Title"); // Different title -> different slug
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.existsBySlugAndRemovedFalse(anyString())).thenReturn(false);
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlog(1, updateBlogRequest,
                "author@test.com");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        verify(blogRepository, times(1)).save(blog);
    }

    @Test
    void updateBlog_SameTitle_ShortCircuitsSlugCheck() {
        // blog.slug is "test-title", blogRequest.titulo is "Test Title" -> newSlug
        // "test-title"
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlog(1, updateBlogRequest,
                "author@test.com");

        assertEquals(200, response.getStatusCode());
        verify(blogRepository, never()).existsBySlugAndRemovedFalse(anyString());
    }

    @Test
    void updateBlog_DifferentTitleSameSlug_ShortCircuits() {
        // blog.titulo is "Test Title", blog.slug is "test-title"
        updateBlogRequest.setTitulo("Test Title!!!"); // Slug will still be "test-title"
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlog(1, updateBlogRequest,
                "author@test.com");

        assertEquals(200, response.getStatusCode());
        assertEquals("Test Title!!!", blog.getTitulo());
        assertEquals("test-title", blog.getSlug());
        verify(blogRepository, never()).existsBySlugAndRemovedFalse(anyString());
    }

    @Test
    void updateBlog_NullTitle_Success() {
        updateBlogRequest.setTitulo(null);
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlog(1, updateBlogRequest,
                "author@test.com");

        assertEquals(200, response.getStatusCode());
        assertEquals("Test Title", blog.getTitulo()); // Remains unchanged
    }

    @Test
    void updateBlog_AllOptionalFields() {
        UpdateBlogRequest request = UpdateBlogRequest.builder()
                .breveDescripcion("New brief")
                .contenidoBlog("New content")
                .publicado(false)
                .descripcionImgPortada("New desc")
                .tags(List.of("new-tag"))
                .build();
        lenient().doNothing().when(storagePort).uploadFile(anyString(), any(), anyLong(), anyString());

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(tagRepository.findByNombre("new-tag")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag());
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlog(1, request,
                "author@test.com");

        assertEquals(200, response.getStatusCode());
        assertEquals("New brief", blog.getBreveDescripcion());
        assertEquals("New content", blog.getContenidoBlog());
        assertFalse(blog.isPublicado());
        assertEquals("New desc", blog.getDescripcionImgPortada());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void updateBlogBanner_Success() {
        org.springframework.web.multipart.MultipartFile bannerFile = mock(
                org.springframework.web.multipart.MultipartFile.class);
        when(bannerFile.isEmpty()).thenReturn(false);
        when(bannerFile.getOriginalFilename()).thenReturn("banner.jpg");
        when(bannerFile.getSize()).thenReturn(100L);
        when(bannerFile.getContentType()).thenReturn("image/jpeg");
        lenient().doNothing().when(storagePort).uploadFile(anyString(), any(), anyLong(), anyString());

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlogBanner(1, bannerFile, "author@test.com");

        assertEquals(200, response.getStatusCode());
        assertTrue(blog.getImgBanner().startsWith("blogs/banners/"));
    }

    @Test
    void updateBlogPortada_Success() {
        org.springframework.web.multipart.MultipartFile portadaFile = mock(
                org.springframework.web.multipart.MultipartFile.class);
        when(portadaFile.isEmpty()).thenReturn(false);
        when(portadaFile.getOriginalFilename()).thenReturn("portada.jpg");
        when(portadaFile.getSize()).thenReturn(100L);
        when(portadaFile.getContentType()).thenReturn("image/jpeg");
        lenient().doNothing().when(storagePort).uploadFile(anyString(), any(), anyLong(), anyString());

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.updateBlogPortada(1, portadaFile, "author@test.com");

        assertEquals(200, response.getStatusCode());
        assertTrue(blog.getImgPortada().startsWith("blogs/portadas/"));
    }

    @Test
    void updateBlog_SlugConflict() {
        updateBlogRequest.setTitulo("Other Title"); // Different title -> different slug
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(blogRepository.existsBySlugAndRemovedFalse(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.updateBlog(1, updateBlogRequest, "author@test.com"));
        assertEquals("Ya existe un blog con este título o uno muy similar. Por favor, intenta con otro.",
                exception.getMessage());
    }

    @Test
    void updateBlog_BlogNotFound() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.updateBlog(1, updateBlogRequest, "author@test.com"));
        assertEquals("Blog no encontrado", exception.getMessage());
    }

    @Test
    void updateBlog_NotOwner() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.updateBlog(1, updateBlogRequest, "other@test.com"));
        assertEquals("No tienes permiso para editar este blog", exception.getMessage());
    }

    @Test
    void deleteBlog_Success() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = blogService.deleteBlog(1, "author@test.com");

        assertNotNull(response);
        assertTrue(blog.isRemoved());
        verify(blogRepository, times(1)).save(blog);
    }

    @Test
    void deleteBlog_BlogNotFound() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.deleteBlog(1, "author@test.com"));
        assertEquals("Blog no encontrado", exception.getMessage());
    }

    @Test
    void deleteBlog_NotOwner() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.deleteBlog(1, "other@test.com"));
        assertEquals("No tienes permiso para eliminar este blog", exception.getMessage());
    }

    @Test
    void getBlogBySlug_Success_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(any(), any(), any()))
                .thenReturn(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "127.0.0.1", "Chrome", null);

        assertNotNull(response);
        assertEquals(1, blog.getVistasCount());
        verify(blogVistaRepository, times(1)).save(any());
        verify(blogRepository, times(1)).save(blog);
    }

    @Test
    void getBlogBySlug_AnonymousUserEmail_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(any(), any(), any()))
                .thenReturn(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "anonymousUser");

        assertNotNull(response);
        assertEquals(1, blog.getVistasCount());
    }

    @Test
    void getBlogBySlug_UserNotFound_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // Line 142
        when(blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(any(), any(), any()))
                .thenReturn(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "none@test.com");

        assertNotNull(response);
        assertEquals(1, blog.getVistasCount());
    }

    @Test
    void getBlogBySlug_AuthenticatedUser_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogVistaRepository.existsByBlogAndUser(any(), any())).thenReturn(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "author@test.com");

        assertNotNull(response);
        assertEquals(1, blog.getVistasCount());
        verify(blogVistaRepository, times(1)).save(any());
    }

    @Test
    void getBlogBySlug_AuthenticatedUser_Liked() {
        LikeByUser like = LikeByUser.builder().user(user).blog(blog).isLike(true).build();
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogVistaRepository.existsByBlogAndUser(any(), any())).thenReturn(true);
        when(likeByUserRepository.findByUserAndBlog(user, blog)).thenReturn(Optional.of(like));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "author@test.com");

        assertNotNull(response);
        assertTrue(response.getData().isLiked());
    }

    @Test
    void getBlogBySlug_AuthenticatedUser_NotLiked_RecordExists() {
        LikeByUser like = LikeByUser.builder().user(user).blog(blog).isLike(false).build();
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogVistaRepository.existsByBlogAndUser(any(), any())).thenReturn(true);
        when(likeByUserRepository.findByUserAndBlog(user, blog)).thenReturn(Optional.of(like));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "author@test.com");

        assertNotNull(response);
        assertFalse(response.getData().isLiked());
    }

    @Test
    void getBlogBySlug_AlreadyViewed_Authenticated() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogVistaRepository.existsByBlogAndUser(any(), any())).thenReturn(true);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "author@test.com");

        assertNotNull(response);
        assertEquals(0, blog.getVistasCount());
        verify(blogVistaRepository, never()).save(any());
    }

    @Test
    void getBlogBySlug_NotPublished_ThrowsException() {
        blog.setPublicado(false);
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.getBlogBySlug("test-title", "ip", "ua", null));
        assertEquals("Este blog aún no se encuentra disponible de manera pública.", exception.getMessage());
    }

    @Test
    void getBlogBySlug_BlogNotFound() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> blogService.getBlogBySlug("none", "ip", "ua", null));
        assertEquals("Blog no encontrado", exception.getMessage());
    }

    @Test
    void getBlogBySlug_AlreadyViewed() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(any(), any(), any()))
                .thenReturn(true);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", null);

        assertNotNull(response);
        assertEquals(0, blog.getVistasCount());
        verify(blogVistaRepository, never()).save(any());
    }

    @Test
    void giveLike_ToggleOn() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(likeByUserRepository.findByUserAndBlog(any(), any())).thenReturn(Optional.empty());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = blogService.giveLike(1, "author@test.com");

        assertNotNull(response);
        assertEquals(1, blog.getLikes());
        assertEquals("Like registrado", response.getMessage());
    }

    @Test
    void giveLike_ToggleOff() {
        LikeByUser like = LikeByUser.builder().user(user).blog(blog).isLike(true).build();
        blog.setLikes(1);

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(likeByUserRepository.findByUserAndBlog(any(), any())).thenReturn(Optional.of(like));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = blogService.giveLike(1, "author@test.com");

        assertNotNull(response);
        assertEquals(0, blog.getLikes());
        assertEquals("Like eliminado", response.getMessage());
    }

    @Test
    void getAllPublicBlogs_Success() {
        Page<Blog> page = new PageImpl<>(Collections.singletonList(blog));
        when(blogRepository.findByPublicadoTrueAndRemovedFalse(any())).thenReturn(page);
        when(blogMapper.toResponse(any())).thenReturn(new BlogResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Page<BlogResponse>> response = blogService.getAllPublicBlogs(PageRequest.of(0, 10),
                "author@test.com");

        assertNotNull(response);
        assertEquals(1, response.getData().getTotalElements());
    }

    @Test
    void getBlogsByUser_Success() {
        Page<Blog> page = new PageImpl<>(Collections.singletonList(blog));
        when(blogRepository.findByAuthorEmailAndRemovedFalse(anyString(), any())).thenReturn(page);
        when(blogMapper.toResponse(any())).thenReturn(new BlogResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Page<BlogResponse>> response = blogService.getBlogsByUser("author@test.com", PageRequest.of(0, 10),
                "author@test.com");

        assertNotNull(response);
        assertEquals(1, response.getData().getTotalElements());
    }

    @Test
    void createBlog_NullTags_Success() {
        blogRequest.setTags(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogMapper.toEntity(any())).thenReturn(blog);
        when(blogRepository.existsBySlugAndRemovedFalse(anyString())).thenReturn(false);
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.createBlog(blogRequest, null, null, "author@test.com");

        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
    }

    @Test
    void giveLike_UserNotFound() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> blogService.giveLike(1, "none@test.com"));
    }

    @Test
    void getAllTags_Success() {
        Tag tag1 = Tag.builder().idTag(1).nombre("java").build();
        Tag tag2 = Tag.builder().idTag(2).nombre("spring").build();
        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<List<TagResponse>> response = blogService.getAllTags();

        assertNotNull(response);
        assertEquals(2, response.getData().size());
        assertEquals("java", response.getData().get(0).getName());
    }

    @Test
    void createBlog_ExistingTag_Success() {
        Tag existingTag = Tag.builder().idTag(1).nombre("java").build();
        blogRequest.setTags(Collections.singletonList("java"));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogMapper.toEntity(any())).thenReturn(blog);
        when(blogRepository.existsBySlugAndRemovedFalse(anyString())).thenReturn(false);
        when(tagRepository.findByNombre("java")).thenReturn(Optional.of(existingTag));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.createBlog(blogRequest, null, null, "author@test.com");

        assertNotNull(response);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void giveLike_ToggleOff_ZeroLikes() {
        LikeByUser like = LikeByUser.builder().user(user).blog(blog).isLike(true).build();
        blog.setLikes(0); // Exceptional case

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(likeByUserRepository.findByUserAndBlog(any(), any())).thenReturn(Optional.of(like));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = blogService.giveLike(1, "author@test.com");

        assertNotNull(response);
        assertEquals(0, blog.getLikes());
    }

    @Test
    void getBlogBySlug_NullUser_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(any(), any(), any()))
                .thenReturn(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", null);

        assertNotNull(response);
        verify(blogVistaRepository).save(any());
    }

    @Test
    void getBlogBySlug_AnonymousUser_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(blogVistaRepository.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(any(), any(), any()))
                .thenReturn(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "anonymousUser");

        assertNotNull(response);
        verify(blogVistaRepository).save(any());
        verify(blogRepository).save(any());
    }

    @Test
    void getBlogBySlug_AuthenticatedUser_NoLikeRecord_UniqueView() {
        when(blogRepository.findBySlugAndRemovedFalse(anyString())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(blogVistaRepository.existsByBlogAndUser(any(), any())).thenReturn(true);
        when(likeByUserRepository.findByUserAndBlog(user, blog)).thenReturn(Optional.empty());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug("test-title", "ip", "ua", "author@test.com");

        assertNotNull(response);
        assertFalse(response.getData().isLiked());
    }

    @Test
    void giveLike_ToggleOff_RemainingLikes() {
        blog.setLikes(10);
        LikeByUser like = LikeByUser.builder().user(user).blog(blog).isLike(true).build();

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(likeByUserRepository.findByUserAndBlog(any(), any())).thenReturn(Optional.of(like));
        when(toastIdGenerator.nextId()).thenReturn(1);

        blogService.giveLike(1, "author@test.com");

        assertEquals(9, blog.getLikes());
    }

    @Test
    void getBlogsByUser_Empty_Success() {
        when(blogRepository.findByAuthorEmailAndRemovedFalse(anyString(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<org.springframework.data.domain.Page<BlogResponse>> response = blogService.getBlogsByUser(
                "author@test.com", org.springframework.data.domain.PageRequest.of(0, 10), "author@test.com");

        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void giveLike_ExistingDislike_ToggleOn() {
        LikeByUser like = LikeByUser.builder().user(user).blog(blog).isLike(false).build();
        blog.setLikes(0);

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(likeByUserRepository.findByUserAndBlog(any(), any())).thenReturn(Optional.of(like));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = blogService.giveLike(1, "author@test.com");

        assertNotNull(response);
        assertTrue(like.isLike());
        assertEquals(1, blog.getLikes());
        assertEquals("Like registrado", response.getMessage());
    }
}
